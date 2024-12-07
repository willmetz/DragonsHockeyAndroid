package com.slapshotapps.dragonshockey.models


data class SeasonStats(val stats: List<GameStats>)

data class GameStats(val gameID: Int, val gameStats: List<PlayerGameStats>)

data class PlayerGameStats(val goals: Int, val assists: Int, val penaltyMinutes: Int, val goalsAgainst: Int, val playerID: Int, val gamePlayed: Boolean)

