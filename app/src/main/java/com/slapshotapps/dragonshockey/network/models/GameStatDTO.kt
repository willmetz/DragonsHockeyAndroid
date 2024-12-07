package com.slapshotapps.dragonshockey.network.models

import com.google.gson.annotations.SerializedName

data class GameStatsDTO(@SerializedName("stats") val stats: List<PlayerGameStatsDTO>?,
                     @SerializedName("gameID") val gameID: Int?)

data class PlayerGameStatsDTO(@SerializedName("assists") val assists: Int?,
                       @SerializedName("goals") val goals: Int?,
                       @SerializedName("goalsAgainst") val goalsAgainst: Int?,
                       @SerializedName("penaltyMinutes") val penaltyMinutes: Int?,
                       @SerializedName("playerID") val playerId: Int?,
                       @SerializedName("present") val present: Boolean?)