package com.sebastianvm.musicplayer.ui

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.mainNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<MainViewModel>(
        destination = NavigationRoute.MainRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        MainScreen(navigationDelegate) { page ->
            Screens(page = page, navigationDelegate = navigationDelegate)
        }
    }
}