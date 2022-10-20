package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.Screen

fun NavGraphBuilder.artistListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<ArtistListViewModel>(
        destination = NavigationRoute.ArtistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        Screen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            ArtistListScreen(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}
