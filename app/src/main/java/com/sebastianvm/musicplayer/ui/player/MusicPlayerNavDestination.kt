package com.sebastianvm.musicplayer.ui.player

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen


fun NavGraphBuilder.musicPlayerNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<MusicPlayerViewModel>(
        destination = NavigationRoute.Player,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            MusicPlayerLayout(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}