package com.slapshotapps.dragonshockey.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.constants.DATABASE_GAME_RESULT_KEY
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.network.models.GameDTO
import com.slapshotapps.dragonshockey.network.models.GameResultDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface SeasonResults{
    data object NoResults : SeasonResults
    data class HasResults(val gamesWithResults: List<GameResultData>) : SeasonResults
}

interface GameResultRepository {
    fun getSeasonRecord(): Flow<SeasonRecordResult>
    fun getGameResult(gameId: Int): Flow<GameResultData>
    fun getAllGameResults(): Flow<SeasonResults>
}

sealed interface SeasonRecordResult{
    data class SeasonRecord(val wins: Int, val losses: Int, val ties: Int, val overtimeLosses: Int) : SeasonRecordResult
}



class GameResultRepositoryImp @Inject constructor(private val database: FirebaseDatabase,
                                                  private  val auth: AuthenticationManager) : GameResultRepository{

    private val gson = Gson()

    override fun getSeasonRecord(): Flow<SeasonRecordResult> {
        return callbackFlow {
            auth.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(SeasonRecordResult.SeasonRecord(0,0,0,0))
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.toList<GameResultDTO>(gson).takeIf { it.isNotEmpty() }?.apply {
                        this@callbackFlow.trySend(getSeasonRecord(this))
                    } ?: this@callbackFlow.trySend(SeasonRecordResult.SeasonRecord(0,0,0,0))
                }
            }

            database.getReference(DATABASE_GAME_RESULT_KEY).addValueEventListener(postListener)

            awaitClose {
                database.getReference(DATABASE_GAME_RESULT_KEY).removeEventListener(postListener)
            }
        }
    }

    override fun getGameResult(gameId: Int): Flow<GameResultData> {
        return callbackFlow {
            auth.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(GameResultData.UnknownResult(gameId))
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.toList<GameResultDTO>(gson).firstOrNull { it?.id == gameId }?.apply {
                        this@callbackFlow.trySend(toGameData(this))
                    } ?: this@callbackFlow.trySend(GameResultData.UnknownResult(gameId))
                }
            }

            database.getReference(DATABASE_GAME_RESULT_KEY).addValueEventListener(postListener)

            awaitClose {
                database.getReference(DATABASE_GAME_RESULT_KEY).removeEventListener(postListener)
            }

        }
    }

    override fun getAllGameResults(): Flow<SeasonResults> {
        return callbackFlow {
            auth.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(SeasonResults.NoResults)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.toList<GameResultDTO>(gson).takeIf { it.isNotEmpty() }?.mapNotNull {
                        it?.let { toGameData(it) }
                    }?.let {results ->
                        this@callbackFlow.trySend(SeasonResults.HasResults(results))
                    } ?: this@callbackFlow.trySend(SeasonResults.NoResults)
                }
            }

            database.getReference(DATABASE_GAME_RESULT_KEY).addValueEventListener(postListener)

            awaitClose {
                database.getReference(DATABASE_GAME_RESULT_KEY).removeEventListener(postListener)
            }

        }
    }

    private fun getSeasonRecord(gameResults: List<GameResultDTO?>) = run {
        val wins = gameResults.count { (it?.teamScore ?: 0) > (it?.opponentScore ?: 0) }
        val losses = gameResults.count { (it?.teamScore ?: 0) < (it?.opponentScore ?: 0) && it?.overtimeLoss != true}
        val ties = gameResults.count { (it?.teamScore ?: 0) == (it?.opponentScore ?: 0) }
        val otl = gameResults.count { (it?.overtimeLoss == true) }

        SeasonRecordResult.SeasonRecord(wins, losses, ties, otl)
    }

    private fun toGameData(result: GameResultDTO) = run {
        when{
            (result.teamScore ?: 0) > (result.opponentScore ?: 0) ->
                GameResultData.Win(result.teamScore ?: 0, result.opponentScore ?: 0, result.id ?: 0)
            (result.teamScore ?: 0) < (result.opponentScore ?: 0) && result.overtimeLoss == false ->
                GameResultData.Loss(result.teamScore ?: 0, result.opponentScore ?: 0, result.id ?: 0)
            (result.teamScore ?: 0) == (result.opponentScore ?: 1) ->
                GameResultData.Tie(result.teamScore ?: 0, result.opponentScore ?: 0, result.id ?: 0)
            result.overtimeLoss == true ->
                GameResultData.OTL(result.teamScore ?: 0, result.opponentScore ?: 0, result.id ?: 0)
            else -> GameResultData.UnknownResult(result.id ?: 0)
        }
    }

}