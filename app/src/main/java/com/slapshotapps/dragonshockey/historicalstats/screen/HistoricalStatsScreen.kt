package com.slapshotapps.dragonshockey.historicalstats.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slapshotapps.dragonshockey.R
import com.slapshotapps.dragonshockey.historicalstats.viewmodel.HistoricalSeasonStats
import com.slapshotapps.dragonshockey.historicalstats.viewmodel.HistoricalStatsScreenState
import com.slapshotapps.dragonshockey.historicalstats.viewmodel.HistoricalStatsViewModel
import com.slapshotapps.dragonshockey.historicalstats.viewmodel.PlayerInfo


@Composable
fun HistoricalStatsScreen(playerId: Int, viewModel: HistoricalStatsViewModel = hiltViewModel<HistoricalStatsViewModel>(),
                          modifier: Modifier = Modifier) {
    viewModel.getData(playerId)

}

@Composable
fun HasStats(data: HistoricalStatsScreenState.DataReady){
    Column(modifier = Modifier.fillMaxSize()) {
        PlayerHeader(data.playerInfo)
        LazyColumn(Modifier.fillMaxWidth()) {
            item { if(data.playerInfo.position != "G") SkaterStatsHeader() else GoalieStatsHeader() }
            items(data.stats.filterIsInstance<HistoricalSeasonStats.SkaterStats>()){
                SkaterSeasonStatLine(if(data.stats.indexOf(it) % 2 == 0) Color.White else Color.Red, it)
            }
            items(data.stats.filterIsInstance<HistoricalSeasonStats.GoalieStats>()){
                GoalieSeasonStatRow(if(data.stats.indexOf(it) % 2 == 0) Color.White else Color.Red, it)
            }
        }
    }
}


@Composable
fun NoStatsAvailable(error: HistoricalStatsScreenState.Error){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text(error.message,
            textAlign = TextAlign.Center, color = Color.Red, fontSize = 24.sp)
    }
}

@Composable
fun PlayerHeader(info: PlayerInfo){
    Row(modifier = Modifier.fillMaxWidth().height(200.dp)){
        Column { Image(painterResource(info.playerImage),
            contentDescription = "player image", modifier = Modifier.height(200.dp)) }
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 24.sp, color = Color.Red)){
                        append("Name:")
                    }
                    withStyle(style = SpanStyle(fontSize = 22.sp)){
                        append(" ${info.playerName}")
                    }
                })
            }
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 24.sp, color = Color.Red)){
                        append("Number:")
                    }
                    withStyle(style = SpanStyle(fontSize = 22.sp)){
                        append(" ${info.number}")
                    }
                })

            }
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 24.sp, color = Color.Red)){
                        append("Position:")
                    }
                    withStyle(style = SpanStyle(fontSize = 22.sp)){
                        append(" ${info.position}")
                    }
                })

            }
        }

    }
}

@Composable
private fun SkaterStatsHeader(){
    Row(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(vertical = 8.dp)) {
        Text("Season", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("GP", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("G", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("A", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("P", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("PIM", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun SkaterSeasonStatLine(backgroundColor: Color, stats: HistoricalSeasonStats.SkaterStats){
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor).padding(vertical = 4.dp)){
        Text(stats.season, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.gamesPlayed, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.goals, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.assists, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.points, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.penaltyMinutes, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun GoalieStatsHeader() {
    Row(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(vertical = 8.dp)){
        Text("Season", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("GP", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("W", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("L", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("T", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("GAA", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("SO", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text("PIM", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}

@Composable
private fun GoalieSeasonStatRow(backgroundColor: Color, stats: HistoricalSeasonStats.GoalieStats){
    Row(modifier = Modifier.fillMaxWidth().background(backgroundColor).padding(vertical = 4.dp)){
        Text(stats.season, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.gamesPlayed, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.wins, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.losses, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.ties, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.goalsAgainstAverage, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.shutouts, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        Text(stats.penaltyMinutes, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
    }
}



@Preview
@Composable
private fun PlayerHeaderPreview(){
    PlayerHeader(PlayerInfo("Steve Yzerman", "19", "F", R.drawable.goalie_helment_transparent))
}

@Preview
@Composable
private fun SkaterStatsPreview(){
    Column {
        SkaterStatsHeader()
        SkaterSeasonStatLine(Color.Red, HistoricalSeasonStats.SkaterStats("2025-3",
            "1", "2", "3", "4", "5"))
        SkaterSeasonStatLine(Color.White, HistoricalSeasonStats.SkaterStats("2025-4",
            "1", "2", "3", "4", "5"))
    }
}

@Preview
@Composable
private fun GoalieSeasonStatsPreview(){
    Column {
        GoalieStatsHeader()
        GoalieSeasonStatRow(Color.Red, HistoricalSeasonStats.GoalieStats("2025-3",
            "1", "2", "3", "4", "5", "6", "7"))
        GoalieSeasonStatRow(Color.White, HistoricalSeasonStats.GoalieStats("2025-4",
            "1", "2", "3", "4", "5", "6", "7"))
    }
}

@Preview
@Composable
private fun NoStatsAvailablePreview(){
    NoStatsAvailable(HistoricalStatsScreenState.Error("No Stats Available"))
}

@Preview
@Composable
private fun HasStatsPreview(){
    val info = PlayerInfo("Chris Osgood", "30", "G", R.drawable.goalie_helment_transparent)
    val stats = listOf(
        HistoricalSeasonStats.GoalieStats("2025-3", "1", "2", "3", "4", "5", "6", "7"),
        HistoricalSeasonStats.GoalieStats("2025-4", "12", "5", "3", "4", "5", "6", "7"),
        HistoricalSeasonStats.GoalieStats("2025-5", "12", "6", "3", "4", "5", "6", "7"))

    HasStats(HistoricalStatsScreenState.DataReady(info, stats))
}