package com.sebastianvm.musicplayer.ui.player

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.musicPlayerNavDestination() {
    screenDestination<MusicPlayerViewModel>(NavigationRoute.Player) { viewModel ->
        MusicPlayerScreen(screenViewModel = viewModel)
    }
}