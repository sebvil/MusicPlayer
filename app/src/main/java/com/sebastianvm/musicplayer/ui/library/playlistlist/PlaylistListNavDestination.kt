package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.playlistListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<PlaylistListViewModel>(
        destination = NavigationRoute.PlaylistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        PlaylistListRoute(
            viewModel = viewModel,
            navigateToPlaylist = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackList(
                        arguments = args
                    )
                )
            },
            openPlaylistContextMenu = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.PlaylistContextMenu(arguments = args)
                )
            },
            navigateBack = navigationDelegate::navigateUp
        )
    }
}