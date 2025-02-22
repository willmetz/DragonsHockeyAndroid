package com.slapshotapps.dragonshockey.widgets

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


@Composable
fun SingleButtonAlertDialog(title: String, msg: String, buttonText: String, onClick: () -> Unit){
    AlertDialog(
        onDismissRequest = { onClick },
        title = { Text(title, color = Color.Black) },
        text = { Text(msg, color = Color.DarkGray) },
        confirmButton = {
            Button(onClick = onClick) {
                Text(buttonText)
            }
        }

    )
}