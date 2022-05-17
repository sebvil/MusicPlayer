package com.sebastianvm.musicplayer.ui.playlist

import android.os.Parcelable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class PlaylistArguments(val playlistId: Long) : Parcelable

fun NavGraphBuilder.playlistNavDestination(navController: NavController) {
    screenDestination<PlaylistViewModel, PlaylistArguments>(NavRoutes.PLAYLIST) { viewModel ->
        PlaylistScreen(
            screenViewModel = viewModel,
            delegate = object : PlaylistScreenNavigationDelegate {
                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
        )
    }
}

fun NavController.navigateToPlaylist(arguments: PlaylistArguments) {
    navigateTo(NavRoutes.PLAYLIST, arguments)
}
