package com.slapshotapps.dragonshockey.admin.editgamestats


sealed interface PlayerEditGameStats {
    data class SkaterStats(val name: String, val position: String, val goals: String,
                           val assists: String, val penaltyMins: String) : PlayerEditGameStats
    data class GoalieStats(val name: String, val goalsAgainst: String, val assists: String,
                           val penaltyMins: String) : PlayerEditGameStats
}

class EditGameStatsViewModel {
}