package com.slapshotapps.dragonshockey.admin.editgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.GameID
import com.slapshotapps.dragonshockey.di.IoDispatcher
import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.AdminRepository
import com.slapshotapps.dragonshockey.repository.ScheduleGameResult
import com.slapshotapps.dragonshockey.usecases.GameUseCaseResult
import com.slapshotapps.dragonshockey.usecases.SingleGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
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
    data class OnError(val msg: String, val title: String) : EditGameEvent
}


@HiltViewModel
class EditGameViewModel @Inject constructor(@GameID private val gameID: Int,
                                            private val gameResultUseCase: SingleGameResultUseCase,
                                            private val adminRepository: AdminRepository,
                                            @IoDispatcher private val ioDispatcher: CoroutineDispatcher): ViewModel() {

    private val gameTimeFormater = DateTimeFormatter.ofPattern("h:mm a")
    private val gameDateFormater = DateTimeFormatter.ofPattern("EEE MMM d")

    private val _editGameEventHandler = MutableSharedFlow<EditGameEvent>()
    val editGameEventHandler : SharedFlow<EditGameEvent> = _editGameEventHandler.asSharedFlow()

    private var originalTeamScore: Int? = null
    private var originalOpponentScore: Int? = null
    private var originalIsOTL: Boolean? = null

    val gameState : StateFlow<EditGameState> =
        gameResultUseCase.getSingleGameAndResult(gameID).map { result ->
            when(result){
                is GameUseCaseResult.GameWithResult -> createReadyGameState(result)
                is GameUseCaseResult.GameWithoutResult -> createReadyGameState(result)
                GameUseCaseResult.ResultUnknown -> EditGameState.OnError("Error Loading Game Info")
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, EditGameState.OnLoading)

    fun onEditGame(teamScore: String, opponentScore: String, isOTL: Boolean) {
        println("score updates: teamScore = $teamScore, opponentScore = $opponentScore, otl = $isOTL")

        val teamScoreInteger = teamScore.toIntOrNull()
        val opponentScoreInteger = opponentScore.toIntOrNull()

        viewModelScope.launch {
            when {
                teamScoreInteger == null || opponentScoreInteger == null -> {
                    _editGameEventHandler.emit(EditGameEvent.OnError(
                        "Team score and Opponent Score are required", "Invalid Input"))
                }
                didGameInfoChange(teamScoreInteger, opponentScoreInteger, isOTL) -> {
                    adminRepository.onUpdateGameResult(teamScoreInteger, opponentScoreInteger, isOTL, gameID)
                    _editGameEventHandler.emit(EditGameEvent.EditGameStats(gameID))
                }

                else -> _editGameEventHandler.emit(EditGameEvent.EditGameStats(gameID))

            }

        }
    }

    private fun didGameInfoChange(teamScore: Int, opponentScore: Int, isOTL: Boolean) = teamScore != originalTeamScore ||
            opponentScore != originalOpponentScore || isOTL != originalIsOTL

    private fun createReadyGameState(result: GameUseCaseResult): EditGameState {
        return when(result){
            is GameUseCaseResult.GameWithResult -> {
                originalTeamScore = getTeamScore(result.gameResult).toIntOrNull()
                originalOpponentScore = getOpponentScore(result.gameResult).toIntOrNull()
                originalIsOTL = isOTL(result.gameResult)
                EditGameState.OnGameReady(
                    result.gameInfo.gameInfo.gameID.toString(),
                    formatGameDate(result.gameInfo.gameInfo.gameTime),
                    formatGameTime(result.gameInfo.gameInfo.gameTime),
                    getTeamScore(result.gameResult),
                    getOpponentScore(result.gameResult),
                    result.gameInfo.gameInfo.opponentName,
                    isOTL(result.gameResult)
                )
            }
            is GameUseCaseResult.GameWithoutResult -> EditGameState.OnGameReady(
                result.gameInfo.gameInfo.gameID.toString(),
                formatGameDate(result.gameInfo.gameInfo.gameTime),
                formatGameTime(result.gameInfo.gameInfo.gameTime),
                "",
                "",
                result.gameInfo.gameInfo.opponentName,
                false
            )
            GameUseCaseResult.ResultUnknown -> EditGameState.OnError("Error Loading Game Info")
        }
    }


    private fun formatGameDate(gameTime: LocalDateTime?) : String{
        return kotlin.runCatching { gameDateFormater.format(gameTime) }.getOrNull() ?: "Unknown Game Date"
    }

    private fun formatGameTime(gameTime: LocalDateTime?) : String{
        return kotlin.runCatching { gameTimeFormater.format(gameTime) }.getOrNull() ?: "Unknown Game Time"
    }

    private fun getTeamScore(gameResult: GameResultData): String {
        return when(gameResult){
            is GameResultData.Loss -> gameResult.teamScore.toString()
            is GameResultData.OTL -> gameResult.teamScore.toString()
            is GameResultData.Tie -> gameResult.teamScore.toString()
            is GameResultData.UnknownResult -> ""
            is GameResultData.Win -> gameResult.teamScore.toString()
        }
    }

    private fun getOpponentScore(gameResult: GameResultData): String {
        return when(gameResult){
            is GameResultData.Loss -> gameResult.opponentScore.toString()
            is GameResultData.OTL -> gameResult.opponentScore.toString()
            is GameResultData.Tie -> gameResult.opponentScore.toString()
            is GameResultData.UnknownResult -> ""
            is GameResultData.Win -> gameResult.opponentScore.toString()
        }
    }

    private fun isOTL(gameResult: GameResultData) : Boolean {
        return gameResult is GameResultData.OTL
    }
}