package com.slapshotapps.dragonshockey.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.SeasonStats
import com.slapshotapps.dragonshockey.network.models.GameStatsDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

import javax.inject.Inject


sealed interface SeasonStatsResult{
    data object Error: SeasonStatsResult
    data object NoResults : SeasonStatsResult
    data class HasStats(val seasonStats: List<SeasonStats>): SeasonStatsResult
}

interface SeasonStatRepository{
    fun getSeasonStats() : Flow<SeasonStatsResult>
}

class SeasonStatsRepositoryImp @Inject constructor(private val database: FirebaseDatabase, private  val authenticationManager: AuthenticationManager) : SeasonStatRepository {
    private val gson = Gson()

    override fun getSeasonStats(): Flow<SeasonStatsResult> {
        return callbackFlow {
            authenticationManager.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(SeasonStatsResult.Error)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //TODO
                    dataSnapshot.toList<GameStatsDTO>(gson).takeIf { it.isNotEmpty() }?.apply {
                        this@callbackFlow.trySend(SeasonStatsResult.NoResults)
                    }?: this@callbackFlow.trySend(SeasonStatsResult.NoResults)
                }
            }

            database.getReference("gameStats").addValueEventListener(postListener)

            awaitClose{
                database.getReference("gameStats").removeEventListener(postListener)
            }
        }
    }
}