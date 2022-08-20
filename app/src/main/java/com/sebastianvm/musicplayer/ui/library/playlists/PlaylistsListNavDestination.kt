package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.playlistsListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<PlaylistListViewModel>(
        destination = NavigationRoute.PlaylistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        PlaylistsListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate
        )
    }
}