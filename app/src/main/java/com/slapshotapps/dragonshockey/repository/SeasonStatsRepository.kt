package com.slapshotapps.dragonshockey.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.constants.DATABASE_GAME_STATS_KEY
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.models.GameStats
import com.slapshotapps.dragonshockey.models.PlayerGameStats
import com.slapshotapps.dragonshockey.network.models.GameStatsDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject




sealed interface SeasonStatsResult{
    data object Error: SeasonStatsResult
    data object NoResults : SeasonStatsResult
    data class HasStats(val seasonStats: List<GameStats>): SeasonStatsResult
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
                    dataSnapshot.toList<GameStatsDTO>(gson).takeIf { it.isNotEmpty() }?.mapNotNull {
                        dto -> toGameStatModel(dto) }?.let { this@callbackFlow.trySend(SeasonStatsResult.HasStats(it))
                    } ?: this@callbackFlow.trySend(SeasonStatsResult.NoResults)
                }
            }

            database.getReference(DATABASE_GAME_STATS_KEY).addValueEventListener(postListener)

            awaitClose{
                database.getReference(DATABASE_GAME_STATS_KEY).removeEventListener(postListener)
            }
        }
    }

    private fun toGameStatModel(dto: GameStatsDTO?) = dto?.run {
            GameStats(gameID ?: 0, stats?.map {
                PlayerGameStats(it.goals ?: 0, it.assists ?: 0, it.penaltyMinutes ?:0,
                    it.goalsAgainst ?: 0, it.playerId ?: 0, it.present ?: false)
            } ?: listOf() )
        }
}