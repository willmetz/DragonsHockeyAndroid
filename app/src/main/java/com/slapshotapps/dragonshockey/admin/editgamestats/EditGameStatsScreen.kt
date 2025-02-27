package com.slapshotapps.dragonshockey.admin.editgamestats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slapshotapps.dragonshockey.ui.theme.Typography


@Composable
fun EditGameStatsScreen(){

}

@Composable
private fun SkaterStatCard(stats: PlayerEditGameStats.SkaterStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Card(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                TextField(value = stats.goals, onValueChange = { onStatsChanged(stats.copy(goals = it)) }, label = { Text("Goals") }, modifier = Modifier.weight(0.3f))

                TextField(value = stats.assists, onValueChange = { onStatsChanged(stats.copy(assists = it)) }, label = { Text("Assists") }, modifier = Modifier.weight(0.3f))

                TextField(value = stats.penaltyMins, onValueChange = { onStatsChanged(stats.copy(penaltyMins = it)) }, label = { Text("PIM") }, modifier = Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
private fun GoalieStatCard(stats: PlayerEditGameStats.GoalieStats, onStatsChanged: (PlayerEditGameStats) -> Unit, modifier: Modifier = Modifier){
    Card(modifier = modifier.then(Modifier.fillMaxWidth())) {
        Column {
            Text(stats.name, style = Typography.titleMedium, modifier = Modifier.padding(start = 4.dp))

            Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
                TextField(value = stats.goalsAgainst,
                    onValueChange = { onStatsChanged(stats.copy(goalsAgainst = it)) },
                    label = { Text("Goals Against") },
                    modifier = Modifier.weight(0.3f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(value = stats.assists,
                    onValueChange = { onStatsChanged(stats.copy(assists = it)) },
                    label = { Text("Assists") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                TextField(value = stats.penaltyMins,
                    onValueChange = { onStatsChanged(stats.copy(penaltyMins = it)) },
                    label = { Text("PIM") },
                    modifier = Modifier.weight(0.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
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