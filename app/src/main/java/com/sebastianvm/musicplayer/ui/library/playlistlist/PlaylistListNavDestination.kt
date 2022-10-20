package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen

fun NavGraphBuilder.playlistListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<PlaylistListViewModel>(
        destination = NavigationRoute.PlaylistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            PlaylistListScreen(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}