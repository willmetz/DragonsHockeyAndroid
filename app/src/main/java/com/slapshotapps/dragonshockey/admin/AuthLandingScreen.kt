package com.slapshotapps.dragonshockey.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slapshotapps.dragonshockey.widgets.SingleButtonAlertDialog


@Composable
fun AuthLandingScreen(gameID: Int, onEditGame: (Int) -> Unit ,modifier: Modifier = Modifier, viewModel: AuthViewModel = hiltViewModel<AuthViewModel>()){
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorContent by remember { mutableStateOf(false) }
    var errorMsg = ""
    var errorTitle = ""

    LaunchedEffect(Unit) {
        viewModel.authEvent.collect{
            when(it){
                AuthEvents.OnAuthComplete -> onEditGame(gameID)
                is AuthEvents.OnAuthError -> {
                    errorMsg = it.msg
                    errorTitle = it.title
                    showErrorContent = true
                }
            }
        }
    }

    if(showErrorContent){
        SingleButtonAlertDialog(errorTitle, errorMsg, "OK") {
            showErrorContent = false
        }
    }

    Column(modifier = modifier.then(Modifier.fillMaxSize())) {
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("User Name") }
        )
        Spacer(Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") }
        )

        Spacer(Modifier.height(26.dp))

        Button(onClick = {viewModel.onLogin(userName, password)}) {
            Text("Login")
        }
    }
}