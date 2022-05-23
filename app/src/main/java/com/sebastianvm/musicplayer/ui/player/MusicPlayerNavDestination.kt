package com.sebastianvm.musicplayer.ui.player

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.musicPlayerNavDestination() {
    screenDestination<MusicPlayerViewModel>(
        destination = NavigationRoute.Player,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        MusicPlayerScreen(screenViewModel = viewModel)
    }
}