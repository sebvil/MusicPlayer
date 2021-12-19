package com.sebastianvm.musicplayer.ui.player

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.musicPlayerNavDestination() {
    composable(NavRoutes.PLAYER) {
        val screenViewModel = hiltViewModel<MusicPlayerViewModel>()
        MusicPlayerScreen(screenViewModel)
    }
}