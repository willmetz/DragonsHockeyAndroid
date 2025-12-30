package com.slapshotapps.dragonshockey.network.models

import androidx.annotation.Keep


data class CareerStatsDTO(
    val stats: List<SeasonsStatsDTO>
)

data class SeasonsStatsDTO(
    val seasonID: String,
    val stats: CareerStatsPlayerDetailsDTO
)

@Keep
data class CareerStatsPlayerDetailsDTO(
    val assists: Int,
    val gamesPlayed: Int,
    val goals: Int,
    val playerID: Int,
    val penaltyMins: Int,
    val shutouts: Int,
    val goalsAgainst: Int
){
    constructor(): this(0,0,0,0,0,0,0)
}