package com.sebastianvm.musicplayer.ui.playlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class TrackSearchArguments(val playlistId: Long) : NavigationArguments

fun NavGraphBuilder.trackSearchNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<TrackSearchViewModel>(
        NavigationRoute.TRACK_SEARCH
    ) { viewModel ->
        TrackSearchScreen(screenViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}

