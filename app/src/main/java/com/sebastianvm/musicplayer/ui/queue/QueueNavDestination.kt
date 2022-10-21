package com.sebastianvm.musicplayer.ui.queue

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.queueNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<QueueViewModel>(
        destination = NavigationRoute.Queue,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        QueueScreen(screenViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}