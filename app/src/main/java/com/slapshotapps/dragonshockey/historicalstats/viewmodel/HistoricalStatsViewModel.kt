package com.slapshotapps.dragonshockey.historicalstats.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.di.GameID
import com.slapshotapps.dragonshockey.di.PlayerID
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepository
import com.slapshotapps.dragonshockey.repository.HistoricalStatResult
import com.slapshotapps.dragonshockey.roster.RosterScreenState
import com.slapshotapps.dragonshockey.usecases.HistoricalStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface HistoricalStatsScreenState{
    data object Loading : HistoricalStatsScreenState
    data class DataReady(val playerInfo: PlayerInfo, val stats: List<HistoricalSeasonStats>) : HistoricalStatsScreenState
    data class Error(val message: String) : HistoricalStatsScreenState
}

data class PlayerInfo(val playerName: String, val number: String, val position: String, @DrawableRes val playerImage: Int)

sealed interface HistoricalSeasonStats{
    data class GoalieStats(val season: String, val gamesPlayed: String, val wins: String,
                           val losses: String, val ties: String, val goalsAgainstAverage: String,
                           val shutouts: String, val penaltyMinutes: String) : HistoricalSeasonStats

    data class SkaterStats(val season: String, val gamesPlayed: String, val goals: String,
                           val assists: String, val points: String, val penaltyMinutes: String) : HistoricalSeasonStats
}

@HiltViewModel
class HistoricalStatsViewModel @Inject constructor(@PlayerID val playerId: Int,
                                                   private val useCase: HistoricalStatsUseCase) : ViewModel() {

    val historicalStatState = StateFlow<HistoricalStatsScreenState>{

    }.stateIn(viewModelScope, SharingStarted.Lazily, RosterScreenState.Loading)


}