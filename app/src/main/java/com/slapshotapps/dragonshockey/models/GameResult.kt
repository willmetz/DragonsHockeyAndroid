package com.slapshotapps.dragonshockey.models

sealed interface GameResultData{
    data object UnknownResult : GameResultData
    data class Win(val teamScore: Int, val opponentScore: Int) : GameResultData
    data class Loss(val teamScore: Int, val opponentScore: Int) : GameResultData
    data class Tie(val teamScore: Int, val opponentScore: Int) : GameResultData
    data class OTL(val teamScore: Int, val opponentScore: Int) : GameResultData
}