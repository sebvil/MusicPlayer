package com.sebastianvm.musicplayer.ui.player

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.musicPlayerNavDestination() {
    composable(NavRoutes.PLAYER) {
        val screenViewModel = hiltViewModel<MusicPlayerViewModel>()
        MusicPlayerScreen(screenViewModel)
    }
}

fun NavController.navigateToPlayer() {
    this.navigate(NavRoutes.PLAYER) {
        popUpTo(this@navigateToPlayer.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}