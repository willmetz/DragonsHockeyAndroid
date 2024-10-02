package com.slapshotapps.dragonshockey.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.repository.RosterRepository
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleResult
import com.slapshotapps.dragonshockey.widgets.NextGame
import com.slapshotapps.dragonshockey.widgets.PreviousGameResult
import com.slapshotapps.dragonshockey.widgets.SeasonRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


sealed interface HomeScreenState{
    data object Loading : HomeScreenState
    data class DataReady(val record: SeasonRecord, val lastGameResult: PreviousGameResult, val nextGame: NextGame) : HomeScreenState
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val rosterRepository: RosterRepository,
                                        private val scheduleRepository: ScheduleRepository,
                                        @IoDispatcher private val ioDispatcher: CoroutineDispatcher) : ViewModel()
{
    val homeScreenState : StateFlow<HomeScreenState> =
        scheduleRepository.getSchedule().map { scheduleResult ->
            val record = SeasonRecord("0", "0", "0", "0")

            HomeScreenState.DataReady(record, PreviousGameResult.NoResult, getNextGame(scheduleResult))
        }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = HomeScreenState.Loading)


    private fun getNextGame(scheduleResult: ScheduleResult) : NextGame{
        return when(scheduleResult){
            is ScheduleResult.HasSchedule -> {
                scheduleResult.seasonSchedule.firstOrNull {
                    it.gameTime?.isAfter(LocalDateTime.now()) == true
                }?.let {
                    NextGame.GameInfo(getGameTime(it.gameTime), it.isHome, it.opponentName, it.rink)
                } ?: NextGame.NoMoreGames("Wait Till Next Season")
            }
            ScheduleResult.NoScheduleAvailable -> NextGame.NoMoreGames("Wait Till Next Season")
        }
    }

    private fun getGameTime(time: LocalDateTime?) : String {
        return kotlin.runCatching {
            DateTimeFormatter.ofPattern("d").format(time).let { dayOfMonth ->
                getDaySuffix(dayOfMonth.toInt()).let{ suffix ->
                    DateTimeFormatter.ofPattern("E M d'$suffix' h:mm a").format(time)
                }
            }
        }.getOrNull() ?: "Unknown"
    }

    private fun getDaySuffix(dayOfMonth: Int) : String {
        return when(dayOfMonth){
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
    }

}