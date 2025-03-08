package com.slapshotapps.dragonshockey.admin.editgame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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


@Composable
fun EditGameScreen(gameViewMode: EditGameViewModel = hiltViewModel<EditGameViewModel>()){
    EditScreenContent()
}

@Composable
fun EditScreenContent(){
    var dragonsScore by remember { mutableStateOf("0") }
    var opponentScore by remember { mutableStateOf("0") }
    var isOtlLoss by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(8.dp))
        GameLabel("Game ID: TBD")
        Spacer(Modifier.height(8.dp))
        GameLabel("Game Date: TBD")
        GameLabel("Game Time: TBD")
        Spacer(Modifier.height(8.dp))

        TextField(dragonsScore, label = {
            Text("Dragons Score")
        }, onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 8.dp, end = 8.dp))

        TextField(opponentScore, label = {
            Text("TBD Score")
        }, onValueChange = {},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 8.dp, end = 8.dp))

        isOTL(isOtlLoss, "OTL:") { isOtlLoss = it }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Button({}) {
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
    EditScreenContent()
}

