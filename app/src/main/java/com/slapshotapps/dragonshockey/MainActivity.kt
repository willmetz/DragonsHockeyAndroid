package com.slapshotapps.dragonshockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.slapshotapps.dragonshockey.home.viewmodel.HomeScreenState
import com.slapshotapps.dragonshockey.home.viewmodel.HomeViewModel
import com.slapshotapps.dragonshockey.ui.theme.DragonsHockeyRefreshTheme
import com.slapshotapps.dragonshockey.widgets.NextGameWidget
import com.slapshotapps.dragonshockey.widgets.SeasonRecordWidget
import com.slapshotapps.dragonshockey.widgets.ShimmerBackground
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragonsHockeyRefreshTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(viewModel.homeScreenState, Modifier.padding(innerPadding))
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.homeScreenState.collect{
                    println("data is $it")
                }
            }
        }
    }
}

@Composable
fun HomeScreen(homeScreenState: StateFlow<HomeScreenState>, modifier: Modifier = Modifier){

    val uiState = homeScreenState.collectAsStateWithLifecycle()

    when(val data = uiState.value){
        is HomeScreenState.DataReady -> HomeScreenContent(data)
        HomeScreenState.Loading -> ShowLoading()
    }




}

@Composable
fun HomeScreenContent(uiState: HomeScreenState.DataReady, modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize())) {
        ShowTeamLogo(Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp))

        SeasonRecordWidget(uiState.record, Modifier.width(160.dp).align(Alignment.CenterHorizontally).padding(bottom = 16.dp))
        NextGameWidget("Next Game", uiState.nextGame, Modifier.align(Alignment.CenterHorizontally))
    }
}

@Composable
fun ShowLoading(modifier: Modifier = Modifier){
    Column(modifier.then(Modifier.fillMaxSize())) {
        ShowTeamLogo(Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp))

        Box(Modifier.width(150.dp).height(50.dp).background(ShimmerBackground()).padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
        Box(Modifier.width(150.dp).height(50.dp).background(ShimmerBackground()).padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
        Box(Modifier.width(150.dp).height(50.dp).background(ShimmerBackground()).padding(bottom = 8.dp).align(Alignment.CenterHorizontally))
    }
}

@Composable
private fun ShowTeamLogo(modifier: Modifier){
    Image(painter = painterResource(R.drawable.dragons_hockey_logo),
        contentDescription = "Dragons Hockey",
        modifier = modifier)
}

