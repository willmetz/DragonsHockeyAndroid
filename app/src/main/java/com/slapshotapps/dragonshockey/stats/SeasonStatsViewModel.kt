package com.slapshotapps.dragonshockey.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.models.GameStats
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerPosition
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.RosterResult
import com.slapshotapps.dragonshockey.repository.SeasonStatRepository
import com.slapshotapps.dragonshockey.repository.SeasonStatsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PlayerData{
    data class SkaterData(val name: String, val position: String, val gamesPlayed: String,
                          val goals: String, val assists: String, val points: String,
                          val penaltyMinutes: String) : PlayerData
    data class GoalieData(val name: String, val position: String, val gamesPlayed: String,
                          val goalsAgainst: String, val goalsAgainstAverage: String, val shutouts: String,
                          val penaltyMinutes: String) : PlayerData
}

sealed interface SeasonStatScreenState{
    data object Loading : SeasonStatScreenState
    data class OnError(val message: String) : SeasonStatScreenState
    data class OnDataReady(val data: List<PlayerData>) : SeasonStatScreenState
    data object NoDataAvailable : SeasonStatScreenState
}

@HiltViewModel
class SeasonStatsViewModel @Inject constructor(seasonStatRepository: SeasonStatRepository, rosterRepository: RosterRepository) : ViewModel() {
    val seasonStatsState : StateFlow<SeasonStatScreenState> = seasonStatRepository.getSeasonStats()
        .combine(rosterRepository.getRoster()){ stats, roster ->
            if(roster is RosterResult.HasRoster) {
                when (stats) {
                    SeasonStatsResult.Error -> SeasonStatScreenState.OnError("Oops, looks like there was an issue.  Check back later")
                    is SeasonStatsResult.HasStats -> SeasonStatScreenState.OnDataReady(
                        buildSeasonStats(stats.seasonStats, roster.players)
                    )

                    SeasonStatsResult.NoResults -> SeasonStatScreenState.NoDataAvailable
                }
            }else{
                SeasonStatScreenState.OnError("Oops, looks like there was an issue.  Check back later")
            }
    }.stateIn(viewModelScope, SharingStarted.Lazily, SeasonStatScreenState.Loading)


    data class LocalStatInfo(var goals: Int, var assists: Int, var gamesPlayed: Int, var pim: Int, var goalsAgainst: Int, var shutouts: Int){
        fun points() = goals + assists
    }
    private fun buildSeasonStats(gameStats: List<GameStats>, roster: List<Player>) : List<PlayerData>{

        val seasonStats = mutableListOf<PlayerData>()
        roster.forEach {player ->
            val playerInfo = LocalStatInfo(0,0,0,0,0, 0)
            gameStats.forEach { stats ->
                stats.gameStats.firstOrNull { statsForGame -> statsForGame.playerID == player.playerID }?.let {
                    playerInfo.pim += it.penaltyMinutes
                    playerInfo.goals += it.goals
                    playerInfo.assists += it.assists
                    playerInfo.goalsAgainst += it.goalsAgainst
                    playerInfo.gamesPlayed += if(it.gamePlayed) 1 else 0
                    if(it.goalsAgainst == 0 && it.gamePlayed) playerInfo.shutouts++
                }
            }

            when(player.position){
                PlayerPosition.Defense, PlayerPosition.Forward -> seasonStats.add(getSkaterData(player, playerInfo))
                PlayerPosition.Goalie -> seasonStats.add(getGoalieData(player, playerInfo))
            }

        }

        return seasonStats
    }

    private fun getSkaterData(player: Player, stats: LocalStatInfo) = PlayerData.SkaterData(getName(player), getPosition(player), stats.gamesPlayed.toString(),
            stats.goals.toString(), stats.assists.toString(), stats.points().toString(), stats.pim.toString() )

    private fun getGoalieData(player: Player, stats: LocalStatInfo) = PlayerData.GoalieData(getName(player), getPosition(player), stats.gamesPlayed.toString(),
        stats.goalsAgainst.toString(), getGoalsAgainstAverage(stats), stats.shutouts.toString(), stats.pim.toString() )

    private fun getName(player: Player) = "${player.firstName} ${player.lastName}"

    private fun getPosition(player: Player) : String{
        return when(player.position){
            PlayerPosition.Forward -> "Forward"
            PlayerPosition.Goalie -> "Goalie"
            PlayerPosition.Defense -> "Defense"
        }
    }

    private fun getGoalsAgainstAverage(stats: LocalStatInfo) =
        String.format(null, "%.2f", stats.goalsAgainst / stats.gamesPlayed.toFloat())

}