package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TrackListArguments(val trackList: TrackList) :
    NavigationArguments

fun NavGraphBuilder.trackListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<TrackListViewModel>(
        destination = NavigationRoute.TrackList,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        TrackListRoute(
            viewModel = viewModel,
            navigateToTrackSearchScreen = { playlistId ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackSearch(
                        TrackSearchArguments(playlistId = playlistId)
                    )
                )
            },
            openSortMenu = { sortableListType ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.SortMenu(
                        SortMenuArguments(
                            sortableListType
                        )
                    )
                )
            },
            navigateBack = { navigationDelegate.navigateUp() })
    }
}
