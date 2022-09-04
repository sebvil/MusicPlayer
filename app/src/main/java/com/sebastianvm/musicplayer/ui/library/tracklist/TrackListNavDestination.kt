package com.sebastianvm.musicplayer.ui.library.tracklist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
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
        TrackListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate
        )
    }
}
