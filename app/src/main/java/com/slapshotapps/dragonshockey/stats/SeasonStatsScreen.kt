package com.slapshotapps.dragonshockey.stats

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SeasonStatsScreen(viewModel: SeasonStatsViewModel = hiltViewModel<SeasonStatsViewModel>()) {
    val data = viewModel.seasonStatsState.collectAsStateWithLifecycle()

    if(data.value is SeasonStatScreenState.loading) Text("Hello world")
}