package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.artistListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<ArtistListViewModel>(
        destination = NavigationRoute.ArtistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        ArtistListRoute(
            viewModel = viewModel,
            openArtistContextMenu = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.ArtistContextMenu(
                        arguments = args
                    )
                )
            },
            navigateToArtistScreen = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.Artist(
                        arguments = args
                    )
                )
            },
            navigateBack = { navigationDelegate.navigateUp() }
        )
    }
}


