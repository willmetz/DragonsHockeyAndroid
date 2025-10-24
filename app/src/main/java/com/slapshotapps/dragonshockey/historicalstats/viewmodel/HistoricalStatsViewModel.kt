package com.slapshotapps.dragonshockey.historicalstats.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slapshotapps.dragonshockey.repository.HistoricalStatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
class HistoricalStatsViewModel @Inject constructor(private val repository: HistoricalStatRepository) : ViewModel() {

    fun getData(){
        viewModelScope.launch {
            repository.getHistoricalStats("1")

        }
    }
}