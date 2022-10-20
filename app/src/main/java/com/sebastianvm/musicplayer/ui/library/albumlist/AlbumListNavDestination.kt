package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.NewScreen


fun NavGraphBuilder.albumListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<AlbumListViewModel>(
        destination = NavigationRoute.AlbumsRoot,
        destinationType = DestinationType.Screen,
    ) { viewModel ->
        val listState = rememberLazyListState()
        NewScreen(
            screenViewModel = viewModel,
            eventHandler = { event ->
                when (event) {
                    is AlbumListUiEvent.ScrollToTop -> {
                        listState.scrollToItem(0)
                    }
                }
            },
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            AlbumListScreen(
                state = state,
                screenDelegate = delegate,
                listState = listState
            )
        }

    }
}