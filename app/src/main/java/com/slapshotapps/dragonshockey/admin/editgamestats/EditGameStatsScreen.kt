package com.slapshotapps.dragonshockey.admin.editgamestats


import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slapshotapps.dragonshockey.ui.theme.Typography
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground


@Composable
fun EditGameStatsScreen(viewModel: EditGameStatsViewModel = hiltViewModel()){

    LaunchedEffect(null) {
        viewModel.fetchData()
    }

    when(val data = viewModel.gameInfo.collectAsStateWithLifecycle().value){
        is EditGameUiState.ErrorLoadingData -> ShowError(data.message)
        is EditGameUiState.HasStats -> {
            HasGameStats(viewModel::onSaveStats, viewModel::onStatsUpdated, data)
        }
        EditGameUiState.Loading -> ShowLoading()
    }
}

@Composable
private fun HasGameStats(onSaveStats: () -> Unit,
                         onStatsUpdated: (PlayerEditGameStats) -> Unit,
                         data: EditGameUiState.HasStats) {
    Column (Modifier.fillMaxSize()) {
        Button(onClick = { onSaveStats() }, Modifier.align(Alignment.CenterHorizontally)) {
            Text("Save Stats")
        }
        PlayerList(data.players, onStatsUpdated)
    }
}

@Composable
fun ShowError(errorMsg: String){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = errorMsg, textAlign = TextAlign.Center)
    }
}


@Composable
fun ShowLoading(modifier: Modifier = Modifier) {
    Column(modifier.then(Modifier.fillMaxSize())) {
        for(i in 0..10){
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(ShimmerBackground())
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
    }
    }
}

@Composable
private fun PlayerList(players: List<PlayerEditGameStats>, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){


    LazyColumn(modifier) {
        items(players.size, key = { players[it].playerID }){

            when(val player = players[it]){
                is PlayerEditGameStats.GoalieStats -> GoalieStatCard(player, {updatedStats -> onStatsChanged(updatedStats)})
                is PlayerEditGameStats.SkaterStats -> SkaterStatCard(player, {updatedStats -> onStatsChanged(updatedStats)})
            }
            if(it < players.size) Spacer(Modifier.fillMaxWidth().height(2.dp).background(Color.Red))
        }
    }
}

@Composable
private fun SkaterStatCard(stats: PlayerEditGameStats.SkaterStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Box(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column(Modifier.background(Color.White).padding(bottom = 16.dp)) {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp).background(Color.White)) {
                StatsTextField(label = "Goals", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(goals = it.text)) }, value =  stats.goals,
                    modifier = Modifier.weight(0.25f))

                Spacer(Modifier.width(4.dp))

                StatsTextField(label = "Assists", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(assists = it.text)) }, value =  stats.assists,
                    modifier = Modifier.weight(0.25f))

                Spacer(Modifier.width(4.dp))

                StatsTextField(label = "PIM", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(penaltyMins = it.text)) }, value =  stats.penaltyMins,
                    modifier = Modifier.weight(0.25f))

                Spacer(Modifier.width(4.dp))

                Column(Modifier.weight(0.25f).background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    GamePlayedToggle(stats.present, {onStatsChanged(stats.copy(present = it))})
                }
            }
        }
    }
}

@Composable
private fun GoalieStatCard(stats: PlayerEditGameStats.GoalieStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Box(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column(Modifier.background(Color.White).padding(bottom = 16.dp)) {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp).background(Color.White)) {
                StatsTextField(label = "GA", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(goalsAgainst = it.text)) }, value =  stats.goalsAgainst,
                    modifier = Modifier.weight(0.25f))
                Spacer(Modifier.width(4.dp))

                StatsTextField(label = "Assists", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(assists = it.text)) }, value =  stats.assists,
                    modifier = Modifier.weight(0.25f))

                Spacer(Modifier.width(4.dp))

                StatsTextField(label = "PIM", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { onStatsChanged(stats.copy(penaltyMins = it.text)) }, value =  stats.penaltyMins,
                    modifier = Modifier.weight(0.25f))

                Spacer(Modifier.width(4.dp))
                
                Column(Modifier.weight(0.25f).background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    GamePlayedToggle(stats.present, {onStatsChanged(stats.copy(present = it))})
                }
            }
        }
    }
}

@Composable
private fun GamePlayedToggle(
    isPresent: Boolean,
    onStatsChanged: (Boolean) -> Unit
) {
    Text("GP")
    Switch(
        isPresent, { onStatsChanged(it) }, colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Red,
            checkedTrackColor = Color.LightGray,
            uncheckedThumbColor = Color.Black,
            uncheckedTrackColor = Color.LightGray
        )
    )
}

@Composable
private fun StatsTextField(label: String, keyboardOptions: KeyboardOptions,
                           value: String, onValueChange: (TextFieldValue) -> Unit, modifier: Modifier){
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        textFieldValue = textFieldValue.copy(
            selection = if (isFocused) {
                TextRange(
                    start = 0,
                    end = textFieldValue.text.length
                )
            } else {
                TextRange.Zero
            }
        )
    }

    TextField(textFieldValue, {
        textFieldValue = it
        onValueChange(it)
    }, modifier,
        keyboardOptions = keyboardOptions,
        label = {Text(label)},
        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
        interactionSource = interactionSource)
}

@Preview
@Composable
private fun GameStatsPreview(){
    val players = mutableListOf<PlayerEditGameStats>()
    players.add(PlayerEditGameStats.SkaterStats("Steve Yzerman", "Forward", "36",  "27", "0", true, 0))
    players.add(PlayerEditGameStats.SkaterStats("Brendan Shanahan", "Forward", "25",  "12", "45", false, 1))
    players.add(PlayerEditGameStats.SkaterStats("Dylan Larkin", "Forward", "10",  "1", "2", true,2))
    players.add(PlayerEditGameStats.SkaterStats("Kirk Maltby", "Forward", "0",  "5", "18", false,3))
    players.add(PlayerEditGameStats.SkaterStats("Kris Draper", "Forward", "0",  "0", "0", true,4))
    players.add(PlayerEditGameStats.GoalieStats("Chris Osgood", "0", "1",  "2", true, 5))

    HasGameStats({}, {}, EditGameUiState.HasStats(players))
}

@Preview
@Composable
private fun PlayerListPreview(){
    val players = mutableListOf<PlayerEditGameStats>()
    players.add(PlayerEditGameStats.SkaterStats("Steve Yzerman", "Forward", "36",  "27", "0", true, 0))
    players.add(PlayerEditGameStats.SkaterStats("Brendan Shanahan", "Forward", "25",  "12", "45", false, 1))
    players.add(PlayerEditGameStats.SkaterStats("Dylan Larkin", "Forward", "10",  "1", "2", true,2))
    players.add(PlayerEditGameStats.SkaterStats("Kirk Maltby", "Forward", "0",  "5", "18", false,3))
    players.add(PlayerEditGameStats.SkaterStats("Kris Draper", "Forward", "0",  "0", "0", true,4))
    players.add(PlayerEditGameStats.GoalieStats("Chris Osgood", "0", "1",  "2", true, 5))

    PlayerList(players, {})
}

@Preview
@Composable
private fun SkaterStatCardPreview(){
    PlayerEditGameStats.SkaterStats("Steve Yzerman", "Forward", "1",  "1", "0", true,2).apply {
        SkaterStatCard(this, {})
    }
}

@Preview
@Composable
private fun GoalieStatCardPreview(){
    PlayerEditGameStats.GoalieStats("Chris Osgood", "0", "1",  "2", false,5).apply {
        GoalieStatCard(this, {})
    }
}

