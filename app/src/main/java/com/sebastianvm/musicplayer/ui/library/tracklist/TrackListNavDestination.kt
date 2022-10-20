package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.util.compose.Screen
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class TrackListArguments(val trackListType: TrackListType, val trackListId: Long) :
    NavigationArguments

fun NavGraphBuilder.trackListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<TrackListViewModel>(
        destination = NavigationRoute.TrackList,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        val listState = rememberLazyListState()
        Screen(
            screenViewModel = viewModel,
            eventHandler = { event ->
                when (event) {
                    is TrackListUiEvent.ScrollToTop -> listState.scrollToItem(0)
                }
            },
            navigationDelegate = navigationDelegate
        ) { state, delegate ->
            TrackListScreen(
                state = state,
                screenDelegate = delegate
            )
        }
    }
}
