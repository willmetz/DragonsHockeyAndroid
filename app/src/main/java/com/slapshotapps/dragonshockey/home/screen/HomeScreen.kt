package com.slapshotapps.dragonshockey.home.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slapshotapps.dragonshockey.R
import com.slapshotapps.dragonshockey.home.viewmodel.HomeScreenState
import com.slapshotapps.dragonshockey.home.viewmodel.HomeViewModel
import com.slapshotapps.dragonshockey.widgets.NextGameWidget
import com.slapshotapps.dragonshockey.widgets.PreviousGameResult
import com.slapshotapps.dragonshockey.widgets.PreviousGameResultWidget
import com.slapshotapps.dragonshockey.widgets.SeasonRecordWidget
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground


@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel<HomeViewModel>(), modifier: Modifier = Modifier) {

    val uiState = homeViewModel.homeScreenState.collectAsStateWithLifecycle()

    when (val data = uiState.value) {
        is HomeScreenState.DataReady -> HomeScreenContent(data)
        HomeScreenState.Loading -> ShowLoading()
    }
}

@Composable
fun HomeScreenContent(uiState: HomeScreenState.DataReady, modifier: Modifier = Modifier) {
    Column(modifier.then(Modifier.fillMaxSize())) {
        ShowTeamLogo(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp))

        SeasonRecordWidget(
            uiState.record,
            Modifier
                .width(160.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        if (uiState.lastGameResult !is PreviousGameResult.NoResult) {
            PreviousGameResultWidget(
                "Last Game",
                uiState.lastGameResult,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }

        NextGameWidget("Next Game", uiState.nextGame, Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun ShowLoading(modifier: Modifier = Modifier) {
    Column(modifier.then(Modifier.fillMaxSize())) {
        ShowTeamLogo(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp))

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
private fun ShowTeamLogo(modifier: Modifier) {
    Image(
        painter = painterResource(R.drawable.dragons_hockey_logo),
        contentDescription = "Dragons Hockey",
        modifier = modifier
    )
}