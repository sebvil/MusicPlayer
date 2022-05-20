package com.sebastianvm.musicplayer.ui.album

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
data class AlbumArguments(val albumId: Long) : NavigationArguments


fun NavGraphBuilder.albumNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<AlbumViewModel>(
        destination = NavigationRoute.Album,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        AlbumScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
        )
    }
}