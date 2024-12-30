package com.slapshotapps.dragonshockey.models

data class Player(val firstName: String,
                  val lastName: String,
                  val number: String,
                  val playerID: Int,
                  val position: PlayerPosition,
                  val shot: Shot)

enum class PlayerPosition{
    Forward,
    Goalie,
    Defense
}

enum class Shot{
    Left,
    Right
}
