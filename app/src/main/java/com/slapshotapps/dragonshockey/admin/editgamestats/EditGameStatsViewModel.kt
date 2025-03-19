package com.slapshotapps.dragonshockey.admin.editgamestats

import androidx.lifecycle.ViewModel
import com.slapshotapps.dragonshockey.di.GameID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


sealed interface PlayerEditGameStats {
    data class SkaterStats(val name: String, val position: String, val goals: String,
                           val assists: String, val penaltyMins: String) : PlayerEditGameStats
    data class GoalieStats(val name: String, val goalsAgainst: String, val assists: String,
                           val penaltyMins: String) : PlayerEditGameStats
}

@HiltViewModel
class EditGameStatsViewModel @Inject constructor(@GameID val gameID: Int): ViewModel() {

    init {
        println("GameID = $gameID")
    }
}