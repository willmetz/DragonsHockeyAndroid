package com.slapshotapps.dragonshockey.usecases

import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.repository.CareerStatsModel
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepository
import com.slapshotapps.dragonshockey.repository.HistoricalStatResult
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

sealed interface PlayerHistoricalStatsResult{
    data object NoStats : PlayerHistoricalStatsResult
    data class HasStats(val careerStatsModel: List<CareerStatsModel>, val playerInfo: Player) : PlayerHistoricalStatsResult
}

class HistoricalStatsUseCase @Inject constructor(private val historicalStatRepository: HistoricalStatRepository,
    private val rosterRepository: RosterRepository) {

    fun getPlayerHistoricalStats(playerID: Int) : Flow<PlayerHistoricalStatsResult>{
        return combine(rosterRepository.getRoster(),
            historicalStatRepository.getHistoricalStats(playerID)) { rosterResult, historicalResult ->
            when(rosterResult) {
                is RosterResult.HasRoster -> {
                    val playerInfo = rosterResult.players.firstOrNull { it.playerID == playerID }
                    if (historicalResult is HistoricalStatResult.HasResults && playerInfo != null) {
                        PlayerHistoricalStatsResult.HasStats(historicalResult.careerStatsModel, playerInfo)
                    }else{
                        PlayerHistoricalStatsResult.NoStats
                    }
                }
                else -> PlayerHistoricalStatsResult.NoStats
            }
        }
    }
}