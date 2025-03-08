package com.slapshotapps.dragonshockey.admin.editgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.ScheduleGameResult
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

@HiltViewModel
class EditGameViewModel @Inject constructor(private val scheduleRepository: ScheduleRepository, @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("h:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    private val _gameState = MutableStateFlow<EditGameState>(EditGameState.OnLoading)
    val gameState : StateFlow<EditGameState> = _gameState.asStateFlow()

    fun onGetGameInfo(gameID: Int){
        viewModelScope.launch(ioDispatcher) {
            scheduleRepository.getGame(gameID).let { result ->
                when(result){
                    is ScheduleGameResult.GameAvailable -> {
                        val data = EditGameState.OnGameReady(
                            result.gameInfo.gameID.toString(),
                            formatGameDate(result.gameInfo.gameTime),
                            formatGameTime(result.gameInfo.gameTime),
                            getTeamScore(result.gameInfo),
                            getOpponentScore(result.gameInfo),
                            result.gameInfo.opponentName,
                            isOTL(result.gameInfo)
                        )

                        _gameState.emit(data)
                    }
                    ScheduleGameResult.GameUnavailable -> {
                        _gameState.emit(EditGameState.OnError("Error Loading Game Info"))
                    }
                }
            }
        }
    }


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