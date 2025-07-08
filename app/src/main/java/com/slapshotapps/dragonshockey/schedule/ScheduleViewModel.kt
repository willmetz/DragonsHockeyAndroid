package com.slapshotapps.dragonshockey.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.usecases.ScheduleGame
import com.slapshotapps.dragonshockey.usecases.ScheduleUseCaseResult
import com.slapshotapps.dragonshockey.usecases.ScheduleWithResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.format.DateTimeFormatter
import javax.inject.Inject



sealed class ScheduleElement(open val gameDate: String, open val gameTime: String, open val opponentName: String, open val home: Boolean, open val gameID: Int){
    data class GameWithResult(override val gameDate: String, override val gameTime: String, override val opponentName: String, override val home: Boolean, override val gameID: Int, val result: GameResultData) :
        ScheduleElement(gameDate, gameTime, opponentName, home, gameID)
    data class Game(override val gameDate: String, override val gameTime: String, override val opponentName: String, override val home: Boolean, override val gameID: Int):
        ScheduleElement(gameDate, gameTime, opponentName, home, gameID)
}

sealed interface ScheduleScreenState{
    data object Loading : ScheduleScreenState
    data object Error : ScheduleScreenState
    data class NoScheduleAvailable(val message: String) : ScheduleScreenState
    data class HasSchedule(val games: List<ScheduleElement>) :ScheduleScreenState
}



@HiltViewModel
class ScheduleViewModel @Inject constructor(useCase: ScheduleWithResultsUseCase) : ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("h:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    val scheduleState : StateFlow<ScheduleScreenState> = useCase.getScheduleWithResults().map {resultData ->
        when(resultData){
            ScheduleUseCaseResult.Error -> {
                ScheduleScreenState.NoScheduleAvailable("Unable to retrieve schedule. Please try again.")
            }
            is ScheduleUseCaseResult.Success -> {
                if (resultData.games.isEmpty()) {
                    ScheduleScreenState.NoScheduleAvailable("No games scheduled at this time.")
                } else {
                    val scheduleElements = resultData.games.sortedBy { it.game.gameTime }.map { game ->
                        when (game) {
                            is ScheduleGame.GameWithResult -> convertToScheduleElementWithResult(game)
                            is ScheduleGame.GameWithoutResult -> convertToScheduleElementWithNoResult(game)
                        }
                    }
                    ScheduleScreenState.HasSchedule(scheduleElements)
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, ScheduleScreenState.Loading)



    private fun convertToScheduleElementWithResult(scheduleItem: ScheduleGame.GameWithResult) : ScheduleElement{
        val gameDate = scheduleItem.game.gameTime?.let { gameDateFormater.format(it) } ?: "Unknown Date"
        val gameTime = scheduleItem.game.gameTime?.let { gameTimeFormater.format(it) } ?: "Unknown Time"
        scheduleItem.game.gameID

        return ScheduleElement.GameWithResult(gameDate, gameTime, scheduleItem.game.opponentName,
            scheduleItem.game.isHome, scheduleItem.game.gameID, scheduleItem.result)


    }

    private fun convertToScheduleElementWithNoResult(scheduleItem: ScheduleGame.GameWithoutResult) : ScheduleElement{
        val gameDate = scheduleItem.game.gameTime?.let { gameDateFormater.format(it) } ?: "Unknown Date"
        val gameTime = scheduleItem.game.gameTime?.let { gameTimeFormater.format(it) } ?: "Unknown Time"
        scheduleItem.game.gameID

        return ScheduleElement.Game(gameDate, gameTime, scheduleItem.game.opponentName, scheduleItem.game.isHome, scheduleItem.game.gameID)
    }
}