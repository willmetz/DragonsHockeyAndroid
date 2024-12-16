package com.slapshotapps.dragonshockey.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slapshotapps.dragonshockey.ui.theme.Typography
import com.slapshotapps.dragonshockey.utils.measureTextWidth

@Composable
fun SeasonStatsScreen(viewModel: SeasonStatsViewModel = hiltViewModel<SeasonStatsViewModel>()) {
    val data = viewModel.seasonStatsState.collectAsStateWithLifecycle()

    if(data.value is SeasonStatScreenState.loading) Text("Hello world")
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
            Text(text = data.gamesPlayed, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = gamesLabelWidth/2 - gamesWidth / 2))
            Text(text = data.goals, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = goalsLabelWidth/2 - goalsWidth/2))
            Text(text = data.assists, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = assistsLabelWidth/2 - assistsWidth/2))
            Text(text = data.points, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = pointsLabelsWidth/2 - pointsWidth/2))
            Text(text = data.penaltyMinutes, style = Typography.titleSmall, modifier = Modifier.weight(0.2f).padding(start = pimLabelWidth/2 - pimWidth/2))
        }
    }
}


@Composable
@Preview
private fun ViewPlayerCard(){
    PlayerStatCard(PlayerData.SkaterData("Wayne Gretzky", "F", "2",
        "4", "5", "9", "3"))
}
