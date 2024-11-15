package com.slapshotapps.dragonshockey.roster

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slapshotapps.dragonshockey.ui.theme.Typography
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground


@Composable
fun RosterScreen(rosterViewModel: RosterViewModel = hiltViewModel<RosterViewModel>(), modifier: Modifier = Modifier){
    val rosterState = rosterViewModel.rosterState.collectAsStateWithLifecycle()

    when(val data = rosterState.value){
        is RosterScreenState.RosterUnavailable -> NoRosterContent()
        is RosterScreenState.HasRoster -> HasRosterContent(data)
        RosterScreenState.Loading -> LoadingContent()
    }

}

@Composable
private fun HasRosterContent(rosterData: RosterScreenState.HasRoster, modifier: Modifier = Modifier) {
    LazyColumn {
        item{
            RosterRow("Number", "Name", "Position", Modifier.background(Color.Red))
        }
        var playerCount = 0
        items(rosterData.players){
            val color = if(playerCount++ % 2 == 0) Color.LightGray else Color.White
            RosterRow(it.number, it.name, it.position, Modifier.background(color))
        }
    }
}

@Composable
private fun NoRosterContent(modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
        Text("Roster Unavailable\nCheck back later", textAlign = TextAlign.Center, style = Typography.bodyLarge )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize())){
        for(i in 1..15){
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(ShimmerBackground())
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun RosterRow(firstColumnText: String, secondColumnText: String, thirdColumnText: String, modifier: Modifier = Modifier){
     Row(modifier.then(
         Modifier
             .fillMaxWidth()
             .height(30.dp)
             .padding(horizontal = 8.dp)), verticalAlignment = Alignment.CenterVertically) {
         Text(firstColumnText, Modifier.weight(0.25f), textAlign = TextAlign.Start)
         Text(secondColumnText, Modifier.weight(0.5f), textAlign = TextAlign.Center)
         Text(thirdColumnText, Modifier.weight(0.25f), textAlign = TextAlign.End)
    }
}


@Preview
@Composable
private fun HeaderAndRowPreview() {
    Column {
        RosterRow("Number", "Name", "Position")
        RosterRow("71", "Dylan Larkin", "Forward", Modifier.background(Color.Gray))
    }
}

@Preview
@Composable
private fun HasRosterContentPreview(){
    val players = listOf(RosterPlayer("F", "Jack Hughes", "25"),
        RosterPlayer("F", "Quinn Hughes", "26"),
        RosterPlayer("F", "Jeff Hughes", "27"),
        RosterPlayer("F", "Jim Hughes", "28"),
        RosterPlayer("F", "Steve Hughes", "29"),
        RosterPlayer("F", "Bob Hughes", "30"))
     HasRosterContent(RosterScreenState.HasRoster(players))
}

@Preview
@Composable
private fun ShimmerPreview(){
    LoadingContent()
}

@Preview
@Composable
private fun NoContentPreview(){
    NoRosterContent()
}