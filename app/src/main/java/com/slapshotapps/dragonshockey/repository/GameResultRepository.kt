package com.slapshotapps.dragonshockey.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.network.models.GameDTO
import com.slapshotapps.dragonshockey.network.models.GameResultDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface GameResultRepository {
    fun getSeasonRecord(): Flow<SeasonRecordResult>
}

sealed interface SeasonRecordResult{
    data class SeasonRecord(val wins: Int, val losses: Int, val ties: Int, val overtimeLosses: Int) : SeasonRecordResult
}

sealed interface GameResults{
    data object NoResults : GameResults
    //data class HasResults( val results: )
}

class GameResultRepositoryImp(private val database: FirebaseDatabase, private  val auth: AuthenticationManager) : GameResultRepository{

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

            database.getReference("gameResults").addValueEventListener(postListener)

            awaitClose {
                database.getReference("gameResults").removeEventListener(postListener)
            }
        }
    }

    //fun getAllGameResults(): Flow<>

    private fun getSeasonRecord(gameResults: List<GameResultDTO?>) = run {
        val wins = gameResults.count { (it?.teamScore ?: 0) > (it?.opponentScore ?: 0) }
        val losses = gameResults.count { (it?.teamScore ?: 0) < (it?.opponentScore ?: 0) && it?.overtimeLoss != true}
        val ties = gameResults.count { (it?.teamScore ?: 0) == (it?.opponentScore ?: 0) }
        val otl = gameResults.count { (it?.overtimeLoss == true) }

        SeasonRecordResult.SeasonRecord(wins, losses, ties, otl)
    }

}