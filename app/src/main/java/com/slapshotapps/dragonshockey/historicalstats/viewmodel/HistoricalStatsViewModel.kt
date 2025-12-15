package com.slapshotapps.dragonshockey.historicalstats.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.R
import com.slapshotapps.dragonshockey.di.PlayerID
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerPosition
import com.slapshotapps.dragonshockey.repository.CareerStatsModel
import com.slapshotapps.dragonshockey.usecases.HistoricalStatsUseCase
import com.slapshotapps.dragonshockey.usecases.PlayerHistoricalStatsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
                        HistoricalStatsScreenState.DataReady(toPlayerInfo(data.playerInfo), getPlayerStats(data.careerStatsModel))
                    }
                }
            }
            PlayerHistoricalStatsResult.NoStats -> HistoricalStatsScreenState.Error("No stats available")
        }
    }

    private fun toPlayerInfo(player: Player) = PlayerInfo("${player.firstName} ${player.lastName}", player.number, getPosition(player), getPlayerImage(player))

    private fun getGoalieStats(stats: List<CareerStatsModel>): List<HistoricalSeasonStats>{

        var careerGoalsAgainst : Int = 0

        val totalStats :MutableList<HistoricalSeasonStats.GoalieStats>  = stats.filterIsInstance<CareerStatsModel.GoalieCareerStats>()
            .map { goalieStat ->
                careerGoalsAgainst += goalieStat.goalsAgainst
                HistoricalSeasonStats.GoalieStats(
                    season = goalieStat.seasonID,
                    gamesPlayed = goalieStat.gamesPlayed.toString(),
                    goalsAgainstAverage = getGoalsAgainstAverage(goalieStat.goalsAgainst, goalieStat.gamesPlayed),
                    shutouts = goalieStat.shutouts.toString(),
                    penaltyMinutes = goalieStat.penaltyMinutes.toString()
                )
            }.sortedByDescending { it.season }.toMutableList()

        totalStats.add(calcGoalieCareerTotals(totalStats, careerGoalsAgainst))

        return totalStats.toList()
    }

    private fun calcGoalieCareerTotals(stats: List<HistoricalSeasonStats.GoalieStats>, careerGoalsAgainst: Int) : HistoricalSeasonStats.GoalieStats{
        val totalGamesPlayed = stats.sumOf { it.gamesPlayed.toInt() }

        return HistoricalSeasonStats.GoalieStats(
            gamesPlayed = totalGamesPlayed.toString(),
            season = "Career",
            goalsAgainstAverage = getGoalsAgainstAverage(careerGoalsAgainst, totalGamesPlayed),
            shutouts = stats.sumOf { it.shutouts.toInt() }.toString(),
            penaltyMinutes = stats.sumOf { it.penaltyMinutes.toInt() }.toString()
        )
    }

    private fun getPlayerStats(stats: List<CareerStatsModel>): List<HistoricalSeasonStats>{
        val totalStats = stats.filterIsInstance<CareerStatsModel.SkaterCareerStats>()
            .map { playerStats ->
                HistoricalSeasonStats.SkaterStats(
                    season = playerStats.seasonID,
                    gamesPlayed = playerStats.gamesPlayed.toString(),
                    penaltyMinutes = playerStats.penaltyMinutes.toString(),
                    goals = playerStats.goals.toString(),
                    assists = playerStats.assists.toString(),
                    points = (playerStats.goals + playerStats.assists).toString()
                )
            }.sortedByDescending { it.season }.toMutableList()

        totalStats.add(calcSkaterCareerTotals(totalStats))

        return totalStats.toList()
    }

    private fun calcSkaterCareerTotals(stats: List<HistoricalSeasonStats.SkaterStats>) : HistoricalSeasonStats.SkaterStats{
        val totalGamesPlayed = stats.sumOf { it.gamesPlayed.toInt() }
        val totalGoals = stats.sumOf { it.goals.toInt() }
        val totalAssists = stats.sumOf { it.assists.toInt() }

        return HistoricalSeasonStats.SkaterStats(
            gamesPlayed = totalGamesPlayed.toString(),
            season = "Career",
            goals = totalGoals.toString(),
            assists = totalAssists.toString(),
            points = (totalGoals + totalAssists).toString(),
            penaltyMinutes = stats.sumOf { it.penaltyMinutes.toInt() }.toString()
        )
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