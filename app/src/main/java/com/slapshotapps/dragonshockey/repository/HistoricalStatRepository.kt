package com.slapshotapps.dragonshockey.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.slapshotapps.dragonshockey.constants.DATABASE_HISTORICAL_STATS_KEY
import com.slapshotapps.dragonshockey.extensions.firebase.toList
import com.slapshotapps.dragonshockey.network.models.CareerStatsDTO
import com.slapshotapps.dragonshockey.network.models.CareerStatsPlayerDetailsDTO
import com.slapshotapps.dragonshockey.network.models.SeasonsStatsDTO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


interface HistoricalStatRepository {
    fun getHistoricalStats(playerID: Int): Flow<HistoricalStatResult>
}

sealed interface HistoricalStatResult{
    data object Error : HistoricalStatResult
    data class HasResults(val careerStatsModel: List<CareerStatsModel>) : HistoricalStatResult
}

sealed interface CareerStatsModel{
    data class SkaterCareerStats(val seasonID: String, val gamesPlayed: Int, val goals: Int, val assists: Int, val penaltyMinutes: Int) : CareerStatsModel
    data class GoalieCareerStats(val seasonID: String, val gamesPlayed: Int, val goals: Int, val assists: Int, val penaltyMinutes: Int, val shutouts: Int, val goalsAgainst: Int) : CareerStatsModel
}

class HistoricalStatRepositoryImp @Inject constructor(private val database: FirebaseDatabase, private  val authenticationManager: AuthenticationManager) : HistoricalStatRepository{
    override fun getHistoricalStats(playerID: Int): Flow<HistoricalStatResult> {
        return callbackFlow {
            authenticationManager.authenticateUserAnonymously()

            val postListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    this@callbackFlow.trySend(HistoricalStatResult.Error)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val statsList = mutableListOf<SeasonsStatsDTO>()
                    for (topLevelSnapshot in dataSnapshot.children) {
                        val seasonID = topLevelSnapshot.child("seasonID").getValue(String::class.java) ?: ""
                        val statsSnapshot = topLevelSnapshot.child("stats")

                        for (stats in statsSnapshot.children) {
                            val playerStats =
                                stats.getValue(CareerStatsPlayerDetailsDTO::class.java)
                            if (playerStats?.playerID == playerID) {
                                statsList.add(SeasonsStatsDTO(seasonID, playerStats))
                            }
                        }
                    }

                    this@callbackFlow.trySend(
                        HistoricalStatResult.HasResults(
                            convertToModel(
                                CareerStatsDTO(statsList)
                            )
                        )
                    )
                }
            }
            database.getReference(DATABASE_HISTORICAL_STATS_KEY).addValueEventListener(postListener)

            awaitClose {
                database.getReference(DATABASE_HISTORICAL_STATS_KEY).removeEventListener(postListener)
            }

        }

    }

    private fun convertToModel(careerStatsDTO: CareerStatsDTO) : List<CareerStatsModel>{
        val careerStatsList = mutableListOf<CareerStatsModel>()
        val isGoalie = careerStatsDTO.stats.getOrNull(0)?.stats?.playerID == GOALIE_PLAYER_ID
        careerStatsDTO.stats.forEach {
            val seasonData = if(isGoalie) {
                CareerStatsModel.GoalieCareerStats(it.seasonID, it.stats.gamesPlayed, it.stats.goals,
                    it.stats.assists, it.stats.penaltyMins, it.stats.shutouts, it.stats.goalsAgainst)
            }else{
                CareerStatsModel.SkaterCareerStats(it.seasonID, it.stats.gamesPlayed, it.stats.goals, it.stats.assists, it.stats.penaltyMins)
            }

            careerStatsList.add(seasonData)
        }

        return careerStatsList
    }

    companion object{
        const val GOALIE_PLAYER_ID = 6
    }

}