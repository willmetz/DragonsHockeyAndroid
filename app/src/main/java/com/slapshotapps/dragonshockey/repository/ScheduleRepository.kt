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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface ScheduleRepository {
    fun getSchedule(): Flow<ScheduleResult>
}

sealed interface ScheduleResult{
    data object NoScheduleAvailable : ScheduleResult
    data class HasSchedule(val seasonSchedule: List<Game>) : ScheduleResult
}

class ScheduleRepositoryImp(private val database: FirebaseDatabase, private  val auth: FirebaseAuth) : ScheduleRepository{

    private val gson = Gson()
    private val gameTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun getSchedule() = callbackFlow {
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

        database.getReference("games").addValueEventListener(postListener)

        awaitClose {
            database.getReference("games").removeEventListener(postListener)
        }
    }

    private fun toSchedule(dtoGames: List<GameDTO?>) = dtoGames.map { gameDto ->
        Game(
            gameDto?.gameID ?: 0,
            getGameTime(gameDto?.gameTime.orEmpty()),
            gameDto?.home ?: false,
            gameDto?.opponent ?: "Unknown",
            gameDto?.rink.orEmpty()
        )
    }

    private fun getGameTime(timeStamp: String) = kotlin.runCatching {
        LocalDateTime.parse(timeStamp, gameTimeFormat)
    }.getOrNull()

}