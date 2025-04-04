package com.slapshotapps.dragonshockey.usecases

import com.slapshotapps.dragonshockey.models.GameStats
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerGameStats
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import com.slapshotapps.dragonshockey.repository.SeasonStatRepository
import com.slapshotapps.dragonshockey.repository.SeasonStatsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

sealed interface SingleGameStats{
    data object Error : SingleGameStats
    data class NoStatsForGame(val players: List<Player>) : SingleGameStats
    data class GameWithStats(val players: List<Player>, val gameStats: GameStats) : SingleGameStats
}

class SingleGameUseCase @Inject constructor(private val seasonStatRepository: SeasonStatRepository,
                                            private val rosterRepository: RosterRepository) {

    fun getGameStats(gameID: Int) : Flow<SingleGameStats>{
        return combine(
            rosterRepository.getRoster(),
            seasonStatRepository.getSeasonStats())
        { roster, seasonStats ->
            if(roster is RosterResult.HasRoster){
                val gameStats = (seasonStats as? SeasonStatsResult.HasStats)?.seasonStats?.firstOrNull{it.gameID == gameID}
                if(gameStats != null){
                    SingleGameStats.GameWithStats(roster.players, gameStats)
                }else{
                    SingleGameStats.NoStatsForGame(roster.players)
                }
            }else{
                SingleGameStats.Error
            }
        }
    }


}