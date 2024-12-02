package com.slapshotapps.dragonshockey.navigation

import androidx.annotation.DrawableRes


data class TopLevelRoutes<T : Any>(val name: String, val route: T, @DrawableRes val iconResource: Int)