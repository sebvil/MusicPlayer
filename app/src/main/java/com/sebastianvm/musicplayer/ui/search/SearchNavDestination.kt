package com.sebastianvm.musicplayer.ui.search

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen

fun NavGraphBuilder.searchNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<SearchViewModel>(
        destination = NavigationRoute.Search,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = {},
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            SearchLayout(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}
