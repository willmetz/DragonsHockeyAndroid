package com.slapshotapps.dragonshockey.usecases

import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.GameResultRepository
import com.slapshotapps.dragonshockey.repository.ScheduleGameResult
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

sealed interface GameUseCaseResult {
    data class GameWithResult(val gameResult: GameResultData, val gameInfo: ScheduleGameResult.GameAvailable) : GameUseCaseResult
    data class GameWithoutResult(val gameInfo: ScheduleGameResult.GameAvailable) : GameUseCaseResult
    data object ResultUnknown : GameUseCaseResult
}

class SingleGameResultUseCase @Inject constructor(private val gameResultRepository: GameResultRepository,
    private val scheduleRepository: ScheduleRepository) {

    fun getSingleGameAndResult(gameId: Int) : Flow<GameUseCaseResult> {
        return combine(
            gameResultRepository.getGameResult(gameId),
            scheduleRepository.getGame(gameId)) { gameResult, gameInfo ->
            when(gameInfo){
                is ScheduleGameResult.GameAvailable -> if(gameResult is GameResultData.UnknownResult){
                    GameUseCaseResult.GameWithoutResult(gameInfo)
                }else {
                    GameUseCaseResult.GameWithResult(gameResult, gameInfo)
                }
                ScheduleGameResult.GameUnavailable -> GameUseCaseResult.ResultUnknown
            }

        }

    }
}