package com.slapshotapps.dragonshockey.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
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
class ScheduleViewModel @Inject constructor(scheduleRepository: ScheduleRepository) : ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("h:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    val scheduleState : StateFlow<ScheduleScreenState> = scheduleRepository.getSchedule().map {resultData ->
        when(resultData){
            is ScheduleResult.HasSchedule -> {
                resultData.seasonSchedule.sortedBy { it.gameTime }.map { convertToScheduleElement(it) }.let { data ->
                    ScheduleScreenState.HasSchedule(data)
                }
            }
            ScheduleResult.NoScheduleAvailable -> ScheduleScreenState.NoScheduleAvailable("")
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, ScheduleScreenState.Loading)



    private fun convertToScheduleElement(game: Game) : ScheduleElement{
        val gameDate = game.gameTime?.let { gameDateFormater.format(it) } ?: "Unknown Date"
        val gameTime = game.gameTime?.let { gameTimeFormater.format(it) } ?: "Unknown Time"
        game.gameID

        return when(game.result){
            GameResultData.UnknownResult -> {
                ScheduleElement.Game(gameDate, gameTime, game.opponentName, game.isHome, game.gameID)
            }
            else -> {
                ScheduleElement.GameWithResult(gameDate, gameTime, game.opponentName, game.isHome, game.gameID, game.result)
            }
        }
    }
}