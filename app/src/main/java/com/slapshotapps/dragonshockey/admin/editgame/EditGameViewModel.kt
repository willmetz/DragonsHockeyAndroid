package com.slapshotapps.dragonshockey.admin.editgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.GameID
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.AdminRepository
import com.slapshotapps.dragonshockey.repository.ScheduleGameResult
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


sealed interface EditGameState{
    data class OnGameReady(val gameID: String, val gameDate: String, val gameTime: String,
                           val teamScore: String, val opponentScore: String,
                           val opponent: String, val isOTL: Boolean) : EditGameState
    data object OnLoading : EditGameState
    data class OnError(val msg: String) : EditGameState
}

sealed interface EditGameEvent{
    data class EditGameStats(val gameID: Int) : EditGameEvent
}

@HiltViewModel
class EditGameViewModel @Inject constructor(@GameID private val gameID: Int,
                                            private val scheduleRepository: ScheduleRepository,
                                            private val adminRepository: AdminRepository,
                                            @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("h:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    private val _editGameEventHandler = MutableSharedFlow<EditGameEvent>()
    val editGameEventHandler : SharedFlow<EditGameEvent> = _editGameEventHandler.asSharedFlow()

    private val _gameState = MutableStateFlow<EditGameState>(EditGameState.OnLoading)
    val gameState : StateFlow<EditGameState> =
        scheduleRepository.getGame(gameID).map { result ->
            when(result){
                is ScheduleGameResult.GameAvailable -> {
                    createReadyGameState(result)
                }
                ScheduleGameResult.GameUnavailable -> {
                    EditGameState.OnError("Error Loading Game Info")
                }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, EditGameState.OnLoading)

    fun onEditGame(teamScore: String, opponentScore: String, isOTL: Boolean){
        println("score updates: teamScore = $teamScore, opponentScore = $opponentScore, otl = $isOTL")
        viewModelScope.launch {
            adminRepository.onUpdateGameResult(teamScore, opponentScore, isOTL, gameID)

            _editGameEventHandler.emit(EditGameEvent.EditGameStats(gameID))
        }
    }

    private fun createReadyGameState(result: ScheduleGameResult.GameAvailable) =
        EditGameState.OnGameReady(
            result.gameInfo.gameID.toString(),
            formatGameDate(result.gameInfo.gameTime),
            formatGameTime(result.gameInfo.gameTime),
            getTeamScore(result.gameInfo),
            getOpponentScore(result.gameInfo),
            result.gameInfo.opponentName,
            isOTL(result.gameInfo)
        )


    private fun formatGameDate(gameTime: LocalDateTime?) : String{
        return kotlin.runCatching { gameDateFormater.format(gameTime) }.getOrNull() ?: "Unknown Game Date"
    }

    private fun formatGameTime(gameTime: LocalDateTime?) : String{
        return kotlin.runCatching { gameTimeFormater.format(gameTime) }.getOrNull() ?: "Unknown Game Time"
    }

    private fun getTeamScore(gameData: Game): String {
        return when(val gameResult = gameData.result){
            is GameResultData.Loss -> gameResult.teamScore.toString()
            is GameResultData.OTL -> gameResult.teamScore.toString()
            is GameResultData.Tie -> gameResult.teamScore.toString()
            GameResultData.UnknownResult -> ""
            is GameResultData.Win -> gameResult.teamScore.toString()
        }
    }

    private fun getOpponentScore(gameData: Game): String {
        return when(val gameResult = gameData.result){
            is GameResultData.Loss -> gameResult.opponentScore.toString()
            is GameResultData.OTL -> gameResult.opponentScore.toString()
            is GameResultData.Tie -> gameResult.opponentScore.toString()
            GameResultData.UnknownResult -> ""
            is GameResultData.Win -> gameResult.opponentScore.toString()
        }
    }

    private fun isOTL(gameData: Game) : Boolean {
        return gameData.result is GameResultData.OTL
    }
}