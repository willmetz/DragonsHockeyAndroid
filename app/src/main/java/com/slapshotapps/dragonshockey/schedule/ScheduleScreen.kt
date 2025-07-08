package com.slapshotapps.dragonshockey.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slapshotapps.dragonshockey.models.GameResultData
import com.slapshotapps.dragonshockey.ui.theme.Typography
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground

private val SCHEDULE_ELEMENT_HEIGHT = 75.dp

@Composable
fun ScheduleScreen(onEditGame: ((Int) -> Unit), viewModel: ScheduleViewModel = hiltViewModel<ScheduleViewModel>())  {
    val state = viewModel.scheduleState.collectAsStateWithLifecycle()

    Column {
        Text("Team Schedule", Modifier.height(50.dp).background(Color.Red).fillMaxWidth().wrapContentHeight(align = Alignment.CenterVertically),
            textAlign = TextAlign.Left, style = Typography.titleLarge, color = Color.White)

        when(val info = state.value){
            is ScheduleScreenState.NoScheduleAvailable -> NoScheduleAvailableScreen(info)
            ScheduleScreenState.Error -> ErrorScreen()
            is ScheduleScreenState.HasSchedule -> {
                LazyColumn {
                    items(info.games){
                        ScheduleItem(it, onEditGame)
                        if(info.games.last() != it){
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray))
                        }
                    }
                }
            }
            ScheduleScreenState.Loading -> LoadingScreen()
        }
    }


}

@Composable
private fun NoScheduleAvailableScreen(data: ScheduleScreenState.NoScheduleAvailable){
    Text(data.message, modifier = Modifier.fillMaxSize())
}

@Composable
private fun ErrorScreen(){
    Text("Unknown error loading schedule, better luck next time.", modifier = Modifier.fillMaxSize())
}

@Composable
private fun LoadingScreen(){
    Column {
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
        Box(Modifier.fillMaxWidth().height(SCHEDULE_ELEMENT_HEIGHT).background(ShimmerBackground()))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleItem(data: ScheduleElement, onEditGame: (Int) -> Unit){
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .height(SCHEDULE_ELEMENT_HEIGHT)
        .combinedClickable(onClick = {}, onLongClick = {onEditGame(data.gameID)})) {
        GameTimeElement(data, Modifier.weight(1f))
        OpponentElement(data, Modifier.weight(1f))
        GameResultElement((data as? ScheduleElement.GameWithResult)?.result, Modifier.weight(1f))
    }

}

@Composable
private fun GameTimeElement(data: ScheduleElement, modifier: Modifier = Modifier){
    Column(verticalArrangement = Arrangement.Center, modifier = modifier.then(Modifier.fillMaxWidth())) {
        Text(data.gameDate, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(data.gameTime, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun OpponentElement(data: ScheduleElement, modifier: Modifier = Modifier){
    Column(verticalArrangement = Arrangement.Center, modifier = modifier.then(Modifier.fillMaxWidth())) {
        Text("vrs", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(data.opponentName, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun GameResultElement(data: GameResultData?, modifier: Modifier = Modifier){
    Column(verticalArrangement = Arrangement.Center, modifier = modifier.then(Modifier.fillMaxWidth())) {
        when (data) {
            is GameResultData.Loss ->
                Text("${data.teamScore} - ${data.opponentScore} (L)", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            is GameResultData.OTL ->
                Text("${data.teamScore} - ${data.opponentScore} (OTL)", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            is GameResultData.Tie ->
                Text("${data.teamScore} - ${data.opponentScore} (T)", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            is GameResultData.Win ->
                Text("${data.teamScore} - ${data.opponentScore} (W)", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            is GameResultData.UnknownResult, null ->
                Text("TBD", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}


@Preview
@Composable
private fun GametimeElementPreview(){
    GameTimeElement(ScheduleElement.Game("Mon Sep 9th", "8:00 PM", "Bob", false, 0))
}

@Preview
@Composable
private fun ScheduleItemPreview(){
    ScheduleItem(ScheduleElement.Game("Mon Sep 9th", "8:00 PM", "Bob", false, 0), {})
}