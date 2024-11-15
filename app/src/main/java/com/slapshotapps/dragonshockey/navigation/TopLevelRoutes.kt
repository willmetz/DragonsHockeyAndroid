package com.slapshotapps.dragonshockey.navigation

import androidx.compose.ui.graphics.vector.ImageVector


data class TopLevelRoutes<T : Any>(val name: String, val route: T, val icon: ImageVector)