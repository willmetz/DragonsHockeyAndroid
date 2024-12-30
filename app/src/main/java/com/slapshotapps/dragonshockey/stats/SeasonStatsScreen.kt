package com.slapshotapps.dragonshockey.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slapshotapps.dragonshockey.ui.theme.Typography
import com.slapshotapps.dragonshockey.utils.measureTextWidth
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground

@Composable
fun SeasonStatsScreen(viewModel: SeasonStatsViewModel = hiltViewModel<SeasonStatsViewModel>()) {
    val data = viewModel.seasonStatsState.collectAsStateWithLifecycle()

    when(val info = data.value){
        SeasonStatScreenState.Loading -> LoadingContent()
        is SeasonStatScreenState.OnError -> ErrorContent(info.message)
        SeasonStatScreenState.NoDataAvailable -> NoContent()
        is SeasonStatScreenState.OnDataReady -> SeasonStatContent(info.data)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize())){
        for(i in 1..8){
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(ShimmerBackground())
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun NoContent(modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text("Stats Unavailable\nCheck back later", textAlign = TextAlign.Center, style = Typography.bodyLarge )
    }
}

@Composable
private fun ErrorContent(errorMsg: String, modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text(errorMsg, textAlign = TextAlign.Center, style = Typography.bodyLarge )
    }
}

@Composable
private fun SeasonStatContent(playerStats: List<PlayerData>, modifier: Modifier = Modifier){
    LazyColumn(modifier.then(Modifier.padding(horizontal = 4.dp))) {
        items(playerStats) {
            Card {
                when(it){
                    is PlayerData.GoalieData -> GoalieStatCard(it)
                    is PlayerData.SkaterData -> PlayerStatCard(it)
                }
            }
            if(playerStats.indexOf(it) != playerStats.size) Spacer(Modifier.height(4.dp))
        }
    }
}


@Composable
private fun PlayerStatCard(data: PlayerData.SkaterData, modifier: Modifier = Modifier){
    val gamesLabelWidth = measureTextWidth("Games", Typography.titleSmall)
    val gamesWidth = measureTextWidth(data.gamesPlayed, Typography.titleSmall)
    val goalsLabelWidth = measureTextWidth("Goals", Typography.titleSmall)
    val goalsWidth = measureTextWidth(data.goals, Typography.titleSmall)
    val assistsLabelWidth = measureTextWidth("Assists", Typography.titleSmall)
    val assistsWidth = measureTextWidth(data.assists, Typography.titleSmall)
    val pointsLabelsWidth = measureTextWidth("Points", Typography.titleSmall)
    val pointsWidth = measureTextWidth(data.points, Typography.titleSmall)
    val pimLabelWidth = measureTextWidth("PIM", Typography.titleSmall)
    val pimWidth = measureTextWidth(data.penaltyMinutes, Typography.titleSmall)

    Column(modifier.then(Modifier.padding(4.dp))) {
        Text("${data.name} (${data.position})", style = Typography.titleMedium)

        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text("Games", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("Goals", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("Assists", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("Points", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("PIM", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
        }

        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text(text = data.gamesPlayed, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(gamesLabelWidth, gamesWidth)))
            Text(text = data.goals, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(goalsLabelWidth, goalsWidth)))
            Text(text = data.assists, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(assistsLabelWidth, assistsWidth)))
            Text(text = data.points, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(pointsLabelsWidth, pointsWidth)))
            Text(text = data.penaltyMinutes, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(pimLabelWidth, pimWidth)))
        }
    }
}

@Composable
private fun GoalieStatCard(data: PlayerData.GoalieData, modifier: Modifier = Modifier){
    val gamesLabelWidth = measureTextWidth("Games", Typography.titleSmall)
    val gamesWidth = measureTextWidth(data.gamesPlayed, Typography.titleSmall)
    val goalsAgainstLabelWidth = measureTextWidth("GA", Typography.titleSmall)
    val goalsAgainstWidth = measureTextWidth(data.goalsAgainst, Typography.titleSmall)
    val gaaLabelWidth = measureTextWidth("GAA", Typography.titleSmall)
    val gaaWidth = measureTextWidth(data.goalsAgainstAverage, Typography.titleSmall)
    val soLabelWidth = measureTextWidth("SO", Typography.titleSmall)
    val soWidth = measureTextWidth(data.shutouts, Typography.titleSmall)
    val pimLabelWidth = measureTextWidth("PIM", Typography.titleSmall)
    val pimWidth = measureTextWidth(data.penaltyMinutes, Typography.titleSmall)

    Column(modifier.then(Modifier.padding(4.dp))) {
        Text("${data.name} (${data.position})", style = Typography.titleMedium)

        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text("Games", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("GA", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("GAA", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("SO", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
            Text("PIM", style = Typography.titleSmall, modifier = Modifier.weight(0.2f))
        }

        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            Text(text = data.gamesPlayed, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(gamesLabelWidth, gamesWidth)))
            Text(text = data.goalsAgainst, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(goalsAgainstLabelWidth, goalsAgainstWidth)))
            Text(text = data.goalsAgainstAverage, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(gaaLabelWidth, gaaWidth)))
            Text(text = data.shutouts, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(soLabelWidth, soWidth)))
            Text(text = data.penaltyMinutes, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = getSafePadding(pimLabelWidth, pimWidth)))
        }
    }
}

private fun getSafePadding(labelLength: Dp, contentLength: Dp) : Dp {
    return (labelLength/2 - contentLength/2).takeIf { it > 0.dp } ?: 0.dp
}

@Composable
@Preview
private fun ViewGoalieCard(){
    GoalieStatCard(PlayerData.GoalieData("Chris Osgood", "G", "3",
        "4", "1.33", "1", "3"))
}

@Composable
@Preview
private fun ViewPlayerCard(){
    PlayerStatCard(PlayerData.SkaterData("Wayne Gretzky", "F", "2",
        "4", "5", "9", "3"))
}
