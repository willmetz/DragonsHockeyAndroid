package com.slapshotapps.dragonshockey.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.constants.DATABASE_SCHEDULE_KEY
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.network.models.GameDTO
import com.slapshotapps.dragonshockey.network.models.GameResultDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume

interface ScheduleRepository {
    fun getSchedule(): Flow<ScheduleResult>
    fun getGame(gameID: Int) : Flow<ScheduleGameResult>
}

sealed interface ScheduleResult{
    data object NoScheduleAvailable : ScheduleResult
    data class HasSchedule(val seasonSchedule: List<Game>) : ScheduleResult
}

sealed interface ScheduleGameResult{
    data object GameUnavailable : ScheduleGameResult
    data class GameAvailable(val gameInfo: Game) : ScheduleGameResult
}

class ScheduleRepositoryImp(private val database: FirebaseDatabase,
                            private val auth: AuthenticationManager,
                            @IoDispatcher val ioDispatcher: CoroutineDispatcher) : ScheduleRepository{

    private val gson = Gson()
    private val gameTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun getSchedule() : Flow<ScheduleResult> {

        return callbackFlow {
            auth.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(ScheduleResult.NoScheduleAvailable)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.toList<GameDTO>(gson).takeIf { it.isNotEmpty() }?.apply {
                        this@callbackFlow.trySend(ScheduleResult.HasSchedule(toSchedule(this)))
                    } ?: this@callbackFlow.trySend(ScheduleResult.NoScheduleAvailable)
                }
            }

            database.getReference(DATABASE_SCHEDULE_KEY).addValueEventListener(postListener)

            awaitClose {
                database.getReference(DATABASE_SCHEDULE_KEY).removeEventListener(postListener)
            }
        }
    }

    override fun getGame(gameID: Int) : Flow<ScheduleGameResult> {
        return flow {
            auth.authenticateUserAnonymously()
            emit(getSingleGameResult(gameID))
        }.flowOn(ioDispatcher)
    }

    private suspend fun getSingleGameResult(gameID: Int) = suspendCancellableCoroutine<ScheduleGameResult> { continuation ->
        database.getReference("games").addValueEventListener( object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.toList<GameDTO>(gson).takeIf { it.isNotEmpty() }?.firstOrNull{it?.gameID == gameID}?.let { gameDTO ->
                    continuation.takeIf { it.isActive }?.resume(ScheduleGameResult.GameAvailable(toGame(gameDTO)))
                }
                database.getReference(DATABASE_SCHEDULE_KEY).removeEventListener(this)
            }

            override fun onCancelled(p0: DatabaseError) {
                continuation.takeIf { it.isActive }?.resume(ScheduleGameResult.GameUnavailable)
                database.getReference(DATABASE_SCHEDULE_KEY).removeEventListener(this)
            }

        })
    }

    private fun toSchedule(dtoGames: List<GameDTO?>) = dtoGames.map { gameDto ->
        gameDto?.let { toGame(it) } ?: Game(0,null, false, "", "", GameResultData.UnknownResult)
    }.sortedBy { it.gameTime }

    private fun toGame(gameDTO: GameDTO) = Game(
            gameDTO.gameID ?: 0,
            getGameTime(gameDTO.gameTime.orEmpty()),
            gameDTO.home ?: false,
            gameDTO.opponent ?: "Unknown",
            gameDTO.rink.orEmpty(),
            toGameData(gameDTO.gameResult ?: GameResultDTO(null, null, null, null))
        )

    private fun getGameTime(timeStamp: String) = kotlin.runCatching {
        LocalDateTime.parse(timeStamp, gameTimeFormat)
    }.getOrNull()

    private fun toGameData(result: GameResultDTO) = run {
        when{
            (result.teamScore ?: 0) > (result.opponentScore ?: 0) ->
                GameResultData.Win(result.teamScore ?: 0, result.opponentScore ?: 0)
            (result.teamScore ?: 0) < (result.opponentScore ?: 0) && result.overtimeLoss == false ->
                GameResultData.Loss(result.teamScore ?: 0, result.opponentScore ?: 0)
            (result.teamScore ?: 0) == (result.opponentScore ?: 1) ->
                GameResultData.Tie(result.teamScore ?: 0, result.opponentScore ?: 0)
            result.overtimeLoss == true ->
                GameResultData.OTL(result.teamScore ?: 0, result.opponentScore ?: 0)
            else -> GameResultData.UnknownResult
        }
    }

}