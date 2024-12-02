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
import com.slapshotapps.dragonshockey.home.screen.HomeScreen
import com.slapshotapps.dragonshockey.navigation.TopLevelRoutes
import com.slapshotapps.dragonshockey.roster.RosterScreen
import com.slapshotapps.dragonshockey.schedule.ScheduleElement
import com.slapshotapps.dragonshockey.schedule.ScheduleScreen
import com.slapshotapps.dragonshockey.ui.theme.DragonsHockeyRefreshTheme
import com.slapshotapps.dragonshockey.ui.theme.Typography
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
    TopLevelRoutes("Home", Home,  R.drawable.home),
    TopLevelRoutes("Stats", Stats, R.drawable.stats),
    TopLevelRoutes("Roster", Roster, R.drawable.roster),
    TopLevelRoutes("Schedule", Schedule, R.drawable.schedule),
    TopLevelRoutes("Settings", Settings, R.drawable.settings)
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
        BottomNavigation(
            backgroundColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            bottomNavRoutes.forEach { topLevelRoute ->
                val selected = currentDestination?.route?.endsWith(topLevelRoute.name) == true
                println("bottom nav state changed: Current Route = ${currentDestination?.route}  top level route = ${topLevelRoute.name}")
                BottomNavigationItem(
                    icon = {
                        Icon(
                            ImageVector.vectorResource(topLevelRoute.iconResource),
                            contentDescription = topLevelRoute.name,
                            tint = if(selected) Color.Red else Color(0.98f, 0.68f, 0.68f)
                        )
                    },
                    label = { Text(text = topLevelRoute.name, style = Typography.bodyMedium, color = Color.Black) },
                    selected = selected,
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




