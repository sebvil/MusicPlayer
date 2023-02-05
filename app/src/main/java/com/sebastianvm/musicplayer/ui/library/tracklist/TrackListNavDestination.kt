package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TrackListArguments(val trackList: TrackList) :
    NavigationArguments

fun NavGraphBuilder.trackListNavDestination(
    navigationDelegate: NavigationDelegate,
) {
    screenDestination<TrackListViewModel>(
        destination = NavigationRoute.TrackList,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        TrackListRoute(
            viewModel = viewModel,
            openTrackContextMenu = { args ->
                navigationDelegate.navigateToScreen(NavigationDestination.TrackContextMenu(args))
            },
            navigateToTrackSearchScreen = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackSearch(args)
                )
            },
            openSortMenu = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.SortMenu(args)
                )
            },
            navigateBack = { navigationDelegate.navigateUp() },
        )
    }
}
