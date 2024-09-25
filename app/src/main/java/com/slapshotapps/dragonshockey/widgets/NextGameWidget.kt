package com.slapshotapps.dragonshockey.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slapshotapps.dragonshockey.ui.theme.Typography


sealed interface NextGame{
    data class NoMoreGames(val title: String) : NextGame
    data class GameInfo(val gameDateTime: String, val isHome: Boolean, val opponent: String,
                        val location: String) : NextGame
}

@Composable
fun NextGameWidget(title: String, gameDetails: NextGame, modifier: Modifier = Modifier){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = title, style = Typography.titleMedium)
        when(gameDetails){
            is NextGame.GameInfo -> {
                GameDateAndHomeStatus(gameDetails.gameDateTime, gameDetails.isHome, Modifier.padding(vertical = 4.dp))
                GameOpponentName(gameDetails.opponent, Modifier.padding(bottom = 4.dp
                ))
                Text(text = gameDetails.location, style = Typography.bodyMedium)
            }
            is NextGame.NoMoreGames -> NoMoreGames(gameDetails.title, Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun GameDateAndHomeStatus(gameTime: String, isHome: Boolean, modifier: Modifier = Modifier){
    val homeAwayText = if(isHome) "Home" else "Guest"
    Row(modifier = modifier) {
        Text(text = gameTime, style = Typography.bodyMedium)
        Text(text = "($homeAwayText)", style = Typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun GameOpponentName(opponent: String, modifier: Modifier = Modifier){
    Text("vrs $opponent", style = Typography.bodyMedium, modifier = modifier)
}

@Composable
private fun NoMoreGames(info: String, modifier: Modifier = Modifier){
    Text(info, style = Typography.titleSmall, modifier = modifier)
}


@Preview
@Composable
private fun HasNextGame(){
    val game = NextGame.GameInfo("Mon Sep 16th 8:50 PM", true, "Badgers", "West Rink")
    NextGameWidget("Next Game", game)
}

@Preview
@Composable
private fun NoMoreGames(){
    NextGameWidget("Next Game", NextGame.NoMoreGames("Wait till next season"))
}
