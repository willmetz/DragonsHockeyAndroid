package com.slapshotapps.dragonshockey.models

import java.time.LocalDateTime


data class Game(val gameID: Int, val gameTime: LocalDateTime?, val isHome: Boolean,
                val opponentName: String, val rink: String, val result: GameResultData)