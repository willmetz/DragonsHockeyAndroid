package com.slapshotapps.dragonshockey.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slapshotapps.dragonshockey.ui.theme.Typography


sealed interface PreviousGameResult{
    data object UpdatePending : PreviousGameResult
    data object NoResult: PreviousGameResult
    data class Win(val teamName: String, val teamScore: String, val opponentName: String, val opponentScore: String) : PreviousGameResult
    data class Loss(val teamName: String, val teamScore: String, val opponentName: String, val opponentScore: String) : PreviousGameResult
    data class Tie(val teamName: String, val teamScore: String, val opponentName: String, val opponentScore: String) : PreviousGameResult
    data class OvertimeLoss(val teamName: String, val teamScore: String, val opponentName: String, val opponentScore: String) : PreviousGameResult
}

@Composable
fun PreviousGameResultWidget(title: String, result: PreviousGameResult, modifier: Modifier = Modifier){
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, style = Typography.titleMedium)
        Spacer(Modifier.padding(vertical = 4.dp))
        GameResultWidget(result)
    }
}

@Composable
private fun GameResultWidget(result: PreviousGameResult, modifier: Modifier = Modifier){
    when(result){
        is PreviousGameResult.Loss -> GameResult(result.opponentName, result.opponentScore, result.teamName, result.teamScore, "L", modifier)
        is PreviousGameResult.OvertimeLoss -> GameResult(result.teamName, result.teamScore, result.opponentName, result.opponentScore, "OTL", modifier)
        is PreviousGameResult.Tie -> GameResult(result.teamName, result.teamScore, result.opponentName, result.opponentScore, "T", modifier)
        is PreviousGameResult.Win -> GameResult(result.teamName, result.teamScore, result.opponentName, result.opponentScore, "W", modifier)
        PreviousGameResult.UpdatePending -> PendingGameResult(modifier)
        PreviousGameResult.NoResult -> Unit
    }
}

@Composable
private fun PendingGameResult(modifier: Modifier = Modifier){
    Text("Game Update Pending", style = Typography.bodyMedium, modifier = modifier)
}

@Composable
private fun GameResult(teamName: String, teamScore: String, opponentName: String,
                       opponentScore: String, resultText: String, modifier: Modifier = Modifier){
    Text(text = "$teamName $teamScore $opponentName $opponentScore ($resultText)", style = Typography.bodyMedium)
}


@Preview
@Composable
private fun ShowResultWithUpdatePending(){
    PreviousGameResultWidget("Last Game", PreviousGameResult.UpdatePending)
}

@Preview
@Composable
private fun ShowResultWithALoss(){
    PreviousGameResultWidget("Last Game", PreviousGameResult.Loss(
        "Dragons", "3", "Tomahawks", "4"))
}

@Preview
@Composable
private fun ShowResultWithAWin(){
    PreviousGameResultWidget("Last Game", PreviousGameResult.Win(
        "Dragons", "4", "Tomahawks", "2"))
}