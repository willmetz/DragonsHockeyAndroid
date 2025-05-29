package com.slapshotapps.dragonshockey.admin.editgamestats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.GameID
import com.slapshotapps.dragonshockey.models.Player
import com.slapshotapps.dragonshockey.models.PlayerGameStats
import com.slapshotapps.dragonshockey.models.PlayerPosition
import com.slapshotapps.dragonshockey.usecases.SingleGameStats
import com.slapshotapps.dragonshockey.usecases.SingleGameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface EditGameUiState {
    data object Loading : EditGameUiState
    data class ErrorLoadingData(val message: String) : EditGameUiState
    data class HasStats(val players: List<PlayerEditGameStats>) : EditGameUiState
}

sealed class PlayerEditGameStats(val playerID: Int) {
    data class SkaterStats(val name: String, val position: String, val goals: String,
                           val assists: String, val penaltyMins: String, val id: Int) : PlayerEditGameStats(id)
    data class GoalieStats(val name: String, val goalsAgainst: String, val assists: String,
                           val penaltyMins: String, val id: Int)  : PlayerEditGameStats(id)
}

@HiltViewModel
class EditGameStatsViewModel @Inject constructor(@GameID val gameID: Int,
                                                 private val singleGameUseCase: SingleGameUseCase): ViewModel() {


    private val _gameInfo : MutableStateFlow<EditGameUiState> = MutableStateFlow(EditGameUiState.Loading)
    val gameInfo: StateFlow<EditGameUiState> = _gameInfo.asStateFlow()


    fun fetchData(){
        viewModelScope.launch {

            singleGameUseCase.getGameStats(gameID).firstOrNull()?.let { gameStats ->
                val result = when(gameStats){
                    SingleGameStats.Error -> EditGameUiState.ErrorLoadingData("Unable to get data, try again.")
                    is SingleGameStats.GameWithStats -> {
                        gameStats.players.map{player ->
                            gameStats.gameStats.gameStats.firstOrNull{it.playerID == player.playerID}.let { stats ->
                                when(player.position){
                                    PlayerPosition.Forward, PlayerPosition.Defense -> buildSkaterStats(player, stats)
                                    PlayerPosition.Goalie -> buildGoalieStats(player, stats)
                                }
                            }
                        }.let { EditGameUiState.HasStats(it) }
                    }
                    is SingleGameStats.NoStatsForGame -> {
                        gameStats.players.map { player ->
                            when(player.position){
                                PlayerPosition.Forward, PlayerPosition.Defense -> buildSkaterStats(player, null)
                                PlayerPosition.Goalie -> buildGoalieStats(player, null)
                            }
                        }.let { EditGameUiState.HasStats(it) }
                    }
                }

                _gameInfo.value = result
            }
        }
    }


    fun onStatsUpdated(stats: PlayerEditGameStats){
        val currentInfo = gameInfo.value
        if(currentInfo is EditGameUiState.HasStats){
                val updatedStats = when(stats){
                    is PlayerEditGameStats.GoalieStats -> updateGoalieStats(stats, currentInfo.players)
                    is PlayerEditGameStats.SkaterStats -> updateSkaterStats(stats, currentInfo.players)

                }
            _gameInfo.value = EditGameUiState.HasStats(updatedStats)
        }

    }

    private fun updateGoalieStats(updatedStats: PlayerEditGameStats.GoalieStats,
                                  currentStats: List<PlayerEditGameStats>) : List<PlayerEditGameStats> {
        return currentStats.map {
            if (updatedStats.playerID == (it as? PlayerEditGameStats.GoalieStats)?.playerID){
                it.copy(goalsAgainst = updatedStats.goalsAgainst, assists = updatedStats.assists, penaltyMins = updatedStats.penaltyMins)
            }else it
        }
    }

    private fun updateSkaterStats(updatedStats: PlayerEditGameStats.SkaterStats,
                                  currentStats: List<PlayerEditGameStats>) : List<PlayerEditGameStats>{

        return currentStats.map {
            if (updatedStats.playerID == (it as? PlayerEditGameStats.SkaterStats)?.playerID){
                it.copy(goals = updatedStats.goals, assists = updatedStats.assists, penaltyMins = updatedStats.penaltyMins)
            }else it
        }
    }

    private fun buildSkaterStats(player: Player, stats: PlayerGameStats?): PlayerEditGameStats.SkaterStats{
        return PlayerEditGameStats.SkaterStats(getName(player), getPosition(player),
            stats?.goals?.toString() ?: "0",
            stats?.assists?.toString() ?: "0",
            stats?.penaltyMinutes?.toString() ?: "0",
            player.playerID)
    }

    private fun buildGoalieStats(goalie: Player, stats: PlayerGameStats?) : PlayerEditGameStats.GoalieStats{
        return PlayerEditGameStats.GoalieStats(getName(goalie),
            stats?.goalsAgainst?.toString()?: "0",
            stats?.assists?.toString()?:"0",
            stats?.penaltyMinutes?.toString() ?: "0",
            goalie.playerID)
    }

    private fun getName(player: Player): String{
        return "${player.firstName} ${player.lastName}"
    }

    private fun getPosition(player: Player): String{
        return when(player.position){
            PlayerPosition.Forward -> "Forward"
            PlayerPosition.Goalie -> "Goalie"
            PlayerPosition.Defense -> "Defense"
        }
    }
}