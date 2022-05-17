package com.sebastianvm.musicplayer.ui.playlist

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize


@kotlinx.serialization.Serializable
@Parcelize
data class PlaylistArguments(val playlistId: Long) : Parcelable

fun NavGraphBuilder.playlistNavDestination() {
    screenDestination<PlaylistViewModel, PlaylistArguments>(NavRoutes.PLAYLIST) { viewModel ->
        PlaylistScreen(screenViewModel = viewModel)
    }
}

fun NavController.navigateToPlaylist(arguments: PlaylistArguments) {
    navigateTo(NavRoutes.PLAYLIST, arguments)
}
