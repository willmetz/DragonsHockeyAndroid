package com.slapshotapps.dragonshockey.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.GameResultRepository
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleResult
import com.slapshotapps.dragonshockey.repository.SeasonRecordResult
import com.slapshotapps.dragonshockey.widgets.NextGame
import com.slapshotapps.dragonshockey.widgets.PreviousGameResult
import com.slapshotapps.dragonshockey.widgets.SeasonRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


sealed interface HomeScreenState{
    data object Loading : HomeScreenState
    data class DataReady(val record: SeasonRecord, val lastGameResult: PreviousGameResult, val nextGame: NextGame) : HomeScreenState
}

@HiltViewModel
class HomeViewModel @Inject constructor(private val scheduleRepository: ScheduleRepository,
                                        private val gameResultRepository: GameResultRepository) : ViewModel()
{
    val homeScreenState : StateFlow<HomeScreenState> =
        scheduleRepository.getSchedule().zip(gameResultRepository.getSeasonRecord()) { scheduleResult, gameResult ->
            HomeScreenState.DataReady(getSeasonRecord(gameResult), getLastGameResult(scheduleResult), getNextGame(scheduleResult))
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

    private suspend fun getLastGameResult(scheduleResult: ScheduleResult) : PreviousGameResult {
        return when(scheduleResult) {
            ScheduleResult.NoScheduleAvailable -> PreviousGameResult.NoResult
            is ScheduleResult.HasSchedule -> getLastGameResultFromGames(scheduleResult.seasonSchedule)
        }
    }

    private fun getLastGame(games: List<Game>): Game?{
        return games.sortedBy { it.gameTime }.lastOrNull { it.gameTime?.isBefore(LocalDateTime.now()) == true }
    }

    private suspend fun getLastGameResultFromGames(games: List<Game>) : PreviousGameResult{
        return getLastGame(games)?.let { game ->
            gameResultRepository.getGameResult(game.gameID).firstOrNull()?.let { gameResult ->
                val opponentName = game.opponentName
                val teamName = "Dragons"

                when(gameResult){
                    is GameResultData.Loss ->
                        PreviousGameResult.Loss(teamName, gameResult.teamScore.toString(), opponentName, gameResult.opponentScore.toString() )
                    is GameResultData.OTL ->
                        PreviousGameResult.OvertimeLoss(teamName, gameResult.teamScore.toString(), opponentName, gameResult.opponentScore.toString() )
                    is GameResultData.Tie ->
                        PreviousGameResult.Tie(teamName, gameResult.teamScore.toString(), opponentName, gameResult.opponentScore.toString() )
                    is GameResultData.Win ->
                        PreviousGameResult.Win(teamName, gameResult.teamScore.toString(), opponentName, gameResult.opponentScore.toString() )
                    is GameResultData.UnknownResult -> PreviousGameResult.UpdatePending
                }
            }
            } ?: PreviousGameResult.NoResult

    }

    private fun getSeasonRecord(seasonRecord: SeasonRecordResult): SeasonRecord{
        return when(seasonRecord){
            is SeasonRecordResult.SeasonRecord -> SeasonRecord(seasonRecord.wins.toString(),
                seasonRecord.losses.toString(),
                seasonRecord.overtimeLosses.toString(),
                seasonRecord.ties.toString(),)
        }
    }

    private fun getGameTime(time: LocalDateTime?) : String {
        return kotlin.runCatching {
            DateTimeFormatter.ofPattern("d").format(time).let { dayOfMonth ->
                getDaySuffix(dayOfMonth.toInt()).let{ suffix ->
                    DateTimeFormatter.ofPattern("E MMM d'$suffix' h:mm a").format(time)
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