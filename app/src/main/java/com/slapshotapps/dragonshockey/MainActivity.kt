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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.slapshotapps.dragonshockey.home.screen.HomeScreen
import com.slapshotapps.dragonshockey.navigation.TopLevelRoutes
import com.slapshotapps.dragonshockey.roster.RosterScreen
import com.slapshotapps.dragonshockey.schedule.ScheduleElement
import com.slapshotapps.dragonshockey.schedule.ScheduleScreen
import com.slapshotapps.dragonshockey.ui.theme.DragonsHockeyRefreshTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable


@Serializable
object Home
@Serializable
object Stats
@Serializable
object Roster
@Serializable
object Schedule
@Serializable
object Settings


val bottomNavRoutes = listOf(
    TopLevelRoutes("Home", Home, Icons.Filled.Home),
    TopLevelRoutes("Stats", Stats, Icons.Filled.Build),
    TopLevelRoutes("Roster", Roster, Icons.Filled.Person),
    TopLevelRoutes("Schedule", Schedule, Icons.Filled.DateRange),
    TopLevelRoutes("Settings", Settings, Icons.Filled.Settings)
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
                    NavHost(navController, startDestination = Home, Modifier.padding(innerPadding)){

                        composable<Home> { HomeScreen() }
                        composable<Roster> { RosterScreen() }
                        composable<Schedule> { ScheduleScreen() }
                    }
                }
            }
        }
    }

    @Composable
    private fun BottomBarComponent(navController: NavHostController) {
        BottomNavigation() {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            bottomNavRoutes.forEach { topLevelRoute ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            topLevelRoute.icon,
                            contentDescription = topLevelRoute.name
                        )
                    },
                    label = { Text(topLevelRoute.name) },
                    selected = currentDestination?.route == topLevelRoute.name,
                    onClick = {
                        navController.navigate(topLevelRoute.route) {
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




