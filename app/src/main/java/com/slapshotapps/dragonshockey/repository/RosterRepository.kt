package com.slapshotapps.dragonshockey.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.network.models.PlayerDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

interface RosterRepository{
    suspend fun getRoster(): Flow<RosterResult>
}

sealed interface RosterResult{
    data class HasRoster(val players: List<Player>) : RosterResult
    data object NoRoster: RosterResult
    data object RosterError: RosterResult
}

class RosterRepositoryImp @Inject constructor(private val database: FirebaseDatabase, private  val authenticationManager: AuthenticationManager) : RosterRepository{

    private val gson = Gson()

    override suspend fun getRoster(): Flow<RosterResult>{

        authenticationManager.authenticateUserAnonymously()

        return callbackFlow {
            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(RosterResult.RosterError)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.toList<PlayerDTO>(gson).takeIf { it.isNotEmpty() }?.apply {
                        this@callbackFlow.trySend(RosterResult.HasRoster(toRoster(this)))
                    }?: this@callbackFlow.trySend(RosterResult.NoRoster)
                }
            }

            database.getReference("roster").addValueEventListener(postListener)

            awaitClose{
                database.getReference("roster").removeEventListener(postListener)
            }
        }
    }

    private fun toRoster(dtoRoster: List<PlayerDTO?>) : List<Player>{
        return dtoRoster.map {
            Player(it?.firstName.orEmpty(),
                it?.lastName.orEmpty(),
                it?.number?.toString() ?: "0",
                it?.playerID?.toInt() ?: 0,
                it?.position?: "F", it?.shot?: "R")
        }
    }

}