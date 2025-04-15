package com.slapshotapps.dragonshockey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.slapshotapps.dragonshockey.admin.AuthLandingScreen
import com.slapshotapps.dragonshockey.admin.editgame.EditGameScreen
import com.slapshotapps.dragonshockey.admin.editgamestats.EditGameStatsScreen
import com.slapshotapps.dragonshockey.home.screen.HomeScreen
import com.slapshotapps.dragonshockey.navigation.TopLevelRoutes
import com.slapshotapps.dragonshockey.roster.RosterScreen
import com.slapshotapps.dragonshockey.schedule.ScheduleElement
import com.slapshotapps.dragonshockey.schedule.ScheduleScreen
import com.slapshotapps.dragonshockey.stats.SeasonStatsScreen
import com.slapshotapps.dragonshockey.ui.theme.DragonsHockeyRefreshTheme
import com.slapshotapps.dragonshockey.ui.theme.Typography
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable



interface BottomNavItem{
    val name: String
    val icon: Int
}

@Serializable
sealed class AppScreen(){
    @Serializable
    data class Home(override val name: String = "Home", override val icon: Int = R.drawable.home) : AppScreen(), BottomNavItem
    @Serializable
    data class Stats(override val name: String = "Stats", override val icon: Int = R.drawable.stats) : AppScreen(), BottomNavItem
    @Serializable
    data class Roster(override val name: String = "Roster", override val icon: Int = R.drawable.roster) : AppScreen(), BottomNavItem
    @Serializable
    data class Schedule(override val name: String = "Schedule", override val icon: Int = R.drawable.schedule) : AppScreen(), BottomNavItem
    @Serializable
    data class AdminLanding(val gameID: Int) : AppScreen()
    @Serializable
    data class AdminEditGame(val gameID: Int): AppScreen()
    @Serializable
    data class AdminEditStats(val gameID: Int): AppScreen()
}

//@Serializable
val bottomNavMenu : List<BottomNavItem> = listOf(
    AppScreen.Home(),
    AppScreen.Stats(),
    AppScreen.Roster(),
    AppScreen.Schedule(),
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DragonsHockeyRefreshTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomBarComponent(navController)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(navController, startDestination = AppScreen.Home(), Modifier.padding(innerPadding)){

                        composable<AppScreen.Home> { HomeScreen() }
                        composable<AppScreen.Roster> { RosterScreen() }
                        composable<AppScreen.Schedule> { ScheduleScreen({ gameID -> navController.navigate(AppScreen.AdminLanding(gameID))}) }
                        composable<AppScreen.Stats> { SeasonStatsScreen() }
                        composable<AppScreen.AdminLanding>{
                            val navEntry = it.toRoute<AppScreen.AdminLanding>()
                            AuthLandingScreen(navEntry.gameID, {gameID ->
                                navController.navigate(AppScreen.AdminEditGame(gameID))
                            })
                        }
                        composable<AppScreen.AdminEditGame>{
                            val navEntry = it.toRoute<AppScreen.AdminEditGame>()
                            it.arguments?.putInt("gameID", navEntry.gameID)
                            EditGameScreen({gameID ->
                                navController.navigate(AppScreen.AdminEditStats(gameID))
                            })
                        }
                        composable<AppScreen.AdminEditStats> {
                            val navEntry = it.toRoute<AppScreen.AdminEditStats>()
                            it.arguments?.putInt("gameID", navEntry.gameID)
                            EditGameStatsScreen()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomBarComponent(navController: NavHostController) {
        BottomNavigation(
            backgroundColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            bottomNavMenu.forEach { route ->
                val selected = currentDestination?.route?.startsWith("com.slapshotapps.dragonshockey.AppScreen.${route.name}") == true
                println("bottom nav state changed: Current Route = ${currentDestination?.route}  top level route = ${route.name}")
                BottomNavigationItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(route.icon),
                            contentDescription = route.name,
                            tint = if(selected) Color.Red else Color(0.98f, 0.68f, 0.68f)
                        )
                    },
                    label = { Text(text = route.name, style = Typography.bodyMedium, color = Color.Black) },
                    selected = selected,
                    onClick = {
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}




