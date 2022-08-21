package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.HasTrackList
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackListComponentArgs
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
object TrackListArguments :
    NavigationArguments, HasTrackList {
    @IgnoredOnParcel
    override val args: TrackListComponentArgs = TrackListComponentArgs(
        trackListId = AllTracksViewModel.ALL_TRACKS,
        trackListType = TrackListType.ALL_TRACKS
    )
}

fun NavGraphBuilder.trackListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<AllTracksViewModel>(
        destination = NavigationRoute.TrackList,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        TrackListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate
        )
    }
}
