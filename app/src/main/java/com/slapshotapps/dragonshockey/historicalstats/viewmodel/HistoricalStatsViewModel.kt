package com.slapshotapps.dragonshockey.historicalstats.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.R
import com.slapshotapps.dragonshockey.di.GameID
import com.slapshotapps.dragonshockey.di.PlayerID
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerPosition
import com.slapshotapps.dragonshockey.repository.CareerStatsModel
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepository
import com.slapshotapps.dragonshockey.repository.HistoricalStatResult
import com.slapshotapps.dragonshockey.roster.RosterScreenState
import com.slapshotapps.dragonshockey.usecases.HistoricalStatsUseCase
import com.slapshotapps.dragonshockey.usecases.PlayerHistoricalStatsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


sealed interface HistoricalStatsScreenState{
    data object Loading : HistoricalStatsScreenState
    data class DataReady(val playerInfo: PlayerInfo, val stats: List<HistoricalSeasonStats>) : HistoricalStatsScreenState
    data class Error(val message: String) : HistoricalStatsScreenState
}

data class PlayerInfo(val playerName: String, val number: String, val position: String, @DrawableRes val playerImage: Int)

sealed interface HistoricalSeasonStats{
    data class GoalieStats(val season: String, val gamesPlayed: String, val goalsAgainstAverage: String,
                           val shutouts: String, val penaltyMinutes: String) : HistoricalSeasonStats

    data class SkaterStats(val season: String, val gamesPlayed: String, val goals: String,
                           val assists: String, val points: String, val penaltyMinutes: String) : HistoricalSeasonStats
}

@HiltViewModel
class HistoricalStatsViewModel @Inject constructor(@PlayerID val playerId: Int,
                                                   private val useCase: HistoricalStatsUseCase) : ViewModel() {

    val historicalStatState: StateFlow<HistoricalStatsScreenState> =
        useCase.getPlayerHistoricalStats(playerId).map {
            convertToScreenState(it)
        }.stateIn(viewModelScope, SharingStarted.Lazily, HistoricalStatsScreenState.Loading)


    fun convertToScreenState(data: PlayerHistoricalStatsResult): HistoricalStatsScreenState{
        return when(data){
            is PlayerHistoricalStatsResult.HasStats -> {
                when{
                    data.playerInfo.position == PlayerPosition.Goalie ->{
                        HistoricalStatsScreenState.DataReady(toPlayerInfo(data.playerInfo), getGoalieStats(data.careerStatsModel))
                    }
                    else -> {
                        HistoricalStatsScreenState.DataReady(toPlayerInfo(data.playerInfo), getGoalieStats(data.careerStatsModel))
                    }
                }
            }
            PlayerHistoricalStatsResult.NoStats -> HistoricalStatsScreenState.Error("No stats available")
        }
    }

    fun toPlayerInfo(player: Player) = PlayerInfo("${player.firstName} ${player.lastName}", player.number, getPosition(player), getPlayerImage(player))

    fun getFirstName(name: String) = name.split(" ").firstOrNull() ?: "Unknown"
    fun getLastName(name: String) = name.split(" ").getOrNull(1) ?: "Unknown"


    fun getGoalieStats(stats: List<CareerStatsModel>): List<HistoricalSeasonStats>{
        return stats.filterIsInstance<CareerStatsModel.GoalieCareerStats>()
            .map { goalieStat ->
                HistoricalSeasonStats.GoalieStats(
                    season = goalieStat.seasonID,
                    gamesPlayed = goalieStat.gamesPlayed.toString(),
                    goalsAgainstAverage = getGoalsAgainstAverage(goalieStat.goalsAgainst, goalieStat.gamesPlayed),
                    shutouts = goalieStat.shutouts.toString(),
                    penaltyMinutes = goalieStat.penaltyMinutes.toString()
                )
            }
    }

    private fun getGoalsAgainstAverage(goalsAgainst: Int, gamesPlayed: Int) : String {
        if (gamesPlayed == 0) {
            return "0.00"
        }
        return String.format(Locale.US, "%.2f", goalsAgainst.toFloat() / gamesPlayed.toFloat())
    }

    private fun getPosition(player: Player): String{
        return when(player.position){
            PlayerPosition.Forward -> "Forward"
            PlayerPosition.Goalie -> "Goalie"
            PlayerPosition.Defense -> "Defense"
        }
    }

    private fun getPlayerImage(player: Player): Int {
        return when (player.position) {
            PlayerPosition.Goalie -> R.drawable.goalie_helment_transparent
            else -> R.drawable.hockey_player_helment_transparent
        }
    }
}