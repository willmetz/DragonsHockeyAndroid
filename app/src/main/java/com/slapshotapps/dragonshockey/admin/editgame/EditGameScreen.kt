package com.slapshotapps.dragonshockey.admin.editgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground
import kotlinx.coroutines.flow.collectLatest


@Composable
fun EditGameScreen(onEditStats: (Int) -> Unit, gameViewModel: EditGameViewModel = hiltViewModel<EditGameViewModel>()){

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        gameViewModel.editGameEventHandler.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect{
            when(it){
                is EditGameEvent.EditGameStats -> onEditStats(it.gameID)
            }
        }
    }

    gameViewModel.gameState.collectAsStateWithLifecycle().value.let {
        when(it){
            is EditGameState.OnError -> {}
            is EditGameState.OnGameReady -> EditScreenContent(it) { gameViewModel.onEditGame() }
            EditGameState.OnLoading -> ShowLoading()
        }
    }
}

@Composable
fun ShowLoading(modifier: Modifier = Modifier) {
    Column(modifier.then(Modifier.fillMaxSize())) {
        Box(
            Modifier
                .width(150.dp)
                .height(50.dp)
                .background(ShimmerBackground())
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Box(
            Modifier
                .width(150.dp)
                .height(50.dp)
                .background(ShimmerBackground())
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Box(
            Modifier
                .width(150.dp)
                .height(50.dp)
                .background(ShimmerBackground())
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun EditScreenContent(state: EditGameState.OnGameReady, onEditStats: () -> Unit){
    var dragonsScore by remember { mutableStateOf(state.teamScore) }
    var opponentScore by remember { mutableStateOf(state.opponentScore) }
    var isOtlLoss by remember { mutableStateOf(state.isOTL) }

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(8.dp))
        GameLabel("Game ID: ${state.gameID}")
        Spacer(Modifier.height(8.dp))
        GameLabel("Game Date: ${state.gameDate}")
        GameLabel("Game Time: ${state.gameTime}")
        Spacer(Modifier.height(8.dp))

        TextField(dragonsScore, label = {
            Text("Dragons Score")
        }, onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 8.dp, end = 8.dp))

        TextField(opponentScore, label = {
            Text("${state.opponent} score")
        }, onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 8.dp, end = 8.dp))

        isOTL(isOtlLoss, "OTL:") { isOtlLoss = it }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Button(onEditStats) {
                Text("Edit Stats")
            }
        }
    }
}

@Composable
fun GameLabel(text: String){
    Text(text, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp))
}

@Composable
fun isOTL(isChecked: Boolean, label: String, onChecked: (Boolean) -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(top=16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label)
            Checkbox(checked = isChecked, onCheckedChange = onChecked)
        }
    }
}


@Preview
@Composable
fun PreviewForEditGame(){
    EditGameState.OnGameReady("12", "12/2/25", "8:00 pm", "3", "0", "Benders", false).let {
        EditScreenContent(it, {})
    }
}

