package com.sebastianvm.musicplayer.ui.search

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.searchNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<SearchViewModel>(
        destination = NavigationRoute.Search,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        SearchScreen(screenViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}
