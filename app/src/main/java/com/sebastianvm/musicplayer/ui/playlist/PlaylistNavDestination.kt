package com.sebastianvm.musicplayer.ui.playlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class PlaylistArguments(val playlistId: Long) : NavigationArguments

fun NavGraphBuilder.playlistNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<PlaylistViewModel>(
        destination = NavigationRoute.Playlist,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        PlaylistScreen(
            screenViewModel = viewModel, navigationDelegate = navigationDelegate
        )
    }
}
