package com.slapshotapps.dragonshockey.admin.editgamestats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slapshotapps.dragonshockey.ui.theme.Typography


@Composable
fun EditGameStatsScreen(){

}

@Composable
private fun PlayerList(players: List<PlayerEditGameStats>, modifier: Modifier = Modifier){
    LazyColumn(modifier) {
        items(players.size){
            when(val player = players[it]){
                is PlayerEditGameStats.GoalieStats -> GoalieStatCard(player, {})
                is PlayerEditGameStats.SkaterStats -> SkaterStatCard(player, {})
            }
            if(it < players.size) Spacer(Modifier.fillMaxWidth().height(2.dp).background(Color.Black))
        }
    }
}

@Composable
private fun SkaterStatCard(stats: PlayerEditGameStats.SkaterStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Box(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column(Modifier.background(Color.Red).padding(bottom = 16.dp)) {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                TextField(value = stats.goals, onValueChange = { onStatsChanged(stats.copy(goals = it)) },
                    label = { Text("Goals") }, modifier = Modifier.weight(0.3f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))

                TextField(value = stats.assists, onValueChange = { onStatsChanged(stats.copy(assists = it)) },
                    label = { Text("Assists") }, modifier = Modifier.weight(0.3f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))

                TextField(value = stats.penaltyMins, onValueChange = { onStatsChanged(stats.copy(penaltyMins = it)) },
                    label = { Text("PIM") }, modifier = Modifier.weight(0.3f),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
            }
        }
    }
}

@Composable
private fun GoalieStatCard(stats: PlayerEditGameStats.GoalieStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Box(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column(Modifier.background(Color.Red).padding(bottom = 16.dp)) {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                TextField(value = stats.goalsAgainst,
                    onValueChange = { onStatsChanged(stats.copy(goalsAgainst = it)) },
                    label = { Text("Goals Against") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                TextField(value = stats.assists,
                    onValueChange = { onStatsChanged(stats.copy(assists = it)) },
                    label = { Text("Assists") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                TextField(value = stats.penaltyMins,
                    onValueChange = { onStatsChanged(stats.copy(penaltyMins = it)) },
                    label = { Text("PIM") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }
        }
    }
}


@Preview
@Composable
private fun PlayerListPreview(){
    val players = mutableListOf<PlayerEditGameStats>()
    players.add(PlayerEditGameStats.SkaterStats("Steve Yzerman", "Forward", "36",  "27", "0"))
    players.add(PlayerEditGameStats.SkaterStats("Brendan Shanahan", "Forward", "25",  "12", "45"))
    players.add(PlayerEditGameStats.SkaterStats("Dylan Larkin", "Forward", "10",  "1", "2"))
    players.add(PlayerEditGameStats.SkaterStats("Kirk Maltby", "Forward", "0",  "5", "18"))
    players.add(PlayerEditGameStats.SkaterStats("Kris Draper", "Forward", "0",  "0", "0"))
    players.add(PlayerEditGameStats.GoalieStats("Chris Osgood", "0", "1",  "2"))

    PlayerList(players)
}

@Preview
@Composable
private fun SkaterStatCardPreview(){
    PlayerEditGameStats.SkaterStats("Steve Yzerman", "Forward", "1",  "1", "0").apply {
        SkaterStatCard(this, {})
    }
}

@Preview
@Composable
private fun GoalieStatCardPreview(){
    PlayerEditGameStats.GoalieStats("Chris Osgood", "0", "1",  "2").apply {
        GoalieStatCard(this, {})
    }
}

