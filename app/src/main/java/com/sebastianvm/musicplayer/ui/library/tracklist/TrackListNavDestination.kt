package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize

// TODO make sealed class to get rid of need of passing id to All tracks list
@kotlinx.serialization.Serializable
@Parcelize
data class TrackListArguments(val trackList: TrackList) :
    NavigationArguments

fun NavGraphBuilder.trackListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<TrackListViewModel>(
        destination = NavigationRoute.TrackList,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        TrackListScreen(viewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}
