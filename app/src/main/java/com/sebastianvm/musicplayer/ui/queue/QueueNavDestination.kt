package com.sebastianvm.musicplayer.ui.queue

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.Screen

fun NavGraphBuilder.queueNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<QueueViewModel>(
        destination = NavigationRoute.Queue,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        val layoutManager = LinearLayoutManager(LocalContext.current)
        Screen(
            screenViewModel = viewModel,
            eventHandler = { event ->
                when (event) {
                    is QueueUiEvent.ScrollToNowPlayingItem -> {
                        layoutManager.scrollToPositionWithOffset(event.index, 0)
                    }
                }
            },
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            QueueLayout(
                state = state,
                screenDelegate = delegate,
                layoutManager = layoutManager
            )
        }
    }
}