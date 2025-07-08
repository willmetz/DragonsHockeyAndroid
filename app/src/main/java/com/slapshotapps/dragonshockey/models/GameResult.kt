package com.slapshotapps.dragonshockey.models

sealed class GameResultData{
    abstract val gameID: Int
    data class UnknownResult(override val gameID: Int) : GameResultData()
    data class Win(val teamScore: Int, val opponentScore: Int, override val gameID: Int) : GameResultData()
    data class Loss(val teamScore: Int, val opponentScore: Int, override val gameID: Int) : GameResultData()
    data class Tie(val teamScore: Int, val opponentScore: Int, override val gameID: Int) : GameResultData()
    data class OTL(val teamScore: Int, val opponentScore: Int, override val gameID: Int) : GameResultData()
}