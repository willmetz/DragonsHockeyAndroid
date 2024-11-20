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



sealed interface ScheduleElement{
    data class GameWithResult(val gameDate: String, val gameTime: String, val opponentName: String, val home: Boolean, val result: GameResultData) : ScheduleElement
    data class Game(val gameDate: String, val gameTime: String, val opponentName: String, val home: Boolean) : ScheduleElement
}

sealed interface ScheduleScreenState{
    data object Loading : ScheduleScreenState
    data object Error : ScheduleScreenState
    data class NoScheduleAvailable(val message: String) : ScheduleScreenState
    data class HasSchedule(val games: List<ScheduleElement>) :ScheduleScreenState
}



@HiltViewModel
class ScheduleViewModel @Inject constructor(scheduleRepository: ScheduleRepository) : ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("H:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    val scheduleState : StateFlow<ScheduleScreenState> = scheduleRepository.getSchedule().map {resultData ->
        when(resultData){
            is ScheduleResult.HasSchedule -> {
                resultData.seasonSchedule.map { convertToScheduleElement(it) }.let { data ->
                    ScheduleScreenState.HasSchedule(data)
                }
            }
            ScheduleResult.NoScheduleAvailable -> ScheduleScreenState.NoScheduleAvailable("")
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, ScheduleScreenState.Loading)



    private fun convertToScheduleElement(game: Game) : ScheduleElement{
        val gameDate = game.gameTime?.let { gameDateFormater.format(it) } ?: "Unknown Date"
        val gameTime = game.gameTime?.let { gameTimeFormater.format(it) } ?: "Unknown Time"

        return when(game.result){
            GameResultData.UnknownResult -> {
                ScheduleElement.Game(gameDate, gameTime, game.opponentName, game.isHome)
            }
            else -> {
                ScheduleElement.GameWithResult(gameDate, gameTime, game.opponentName, game.isHome, game.result)
            }
        }
    }
}