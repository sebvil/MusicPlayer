package com.sebastianvm.musicplayer.ui.library.root

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen


fun NavGraphBuilder.libraryNavDestination(
    navigationDelegate: NavigationDelegate,
) {
    screenDestination<LibraryViewModel>(
        destination = NavigationRoute.LibraryRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            LibraryScreen(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}