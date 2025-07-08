package com.slapshotapps.dragonshockey.usecases

import com.slapshotapps.dragonshockey.models.Game
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.repository.GameResultRepository
import com.slapshotapps.dragonshockey.repository.ScheduleRepository
import com.slapshotapps.dragonshockey.repository.ScheduleResult
import com.slapshotapps.dragonshockey.repository.SeasonResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


sealed class ScheduleGame{
    abstract val game: Game
    data class GameWithResult(override val game: Game, val result: GameResultData): ScheduleGame()
    data class GameWithoutResult(override val game: Game): ScheduleGame()
}

sealed interface ScheduleUseCaseResult{
    data class Success(val games: List<ScheduleGame>): ScheduleUseCaseResult
    data object Error: ScheduleUseCaseResult
}

class ScheduleWithResultsUseCase @Inject constructor(private val scheduleRepository: ScheduleRepository,
                                                     private val resultsRepository: GameResultRepository)  {
    fun getScheduleWithResults(): Flow<ScheduleUseCaseResult>{
        return combine(scheduleRepository.getSchedule(), resultsRepository.getAllGameResults()){ schedule, results ->
            when (schedule) {
                is ScheduleResult.HasSchedule -> {
                    val resultsMap = (results as? SeasonResults.HasResults)
                        ?.gamesWithResults
                        ?.associateBy { it.gameID }
                        ?: emptyMap()

                    val scheduledGames = schedule.seasonSchedule.map { seasonGame ->
                        resultsMap[seasonGame.gameID]?.let { gameResult ->
                            ScheduleGame.GameWithResult(seasonGame, gameResult)
                        } ?: ScheduleGame.GameWithoutResult(seasonGame)
                    }
                    ScheduleUseCaseResult.Success(scheduledGames)
                }
                ScheduleResult.NoScheduleAvailable -> ScheduleUseCaseResult.Error
            }

        }
    }

}