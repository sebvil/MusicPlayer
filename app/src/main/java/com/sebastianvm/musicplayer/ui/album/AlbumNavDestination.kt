package com.sebastianvm.musicplayer.ui.album

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AlbumArguments(val albumId: Long) : NavigationArguments


fun NavGraphBuilder.albumNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<AlbumViewModel>(NavigationRoute.Album) { viewModel ->
        AlbumScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
            delegate = object : AlbumNavigationDelegate {

                override fun openContextMenu(trackId: Long, albumId: Long, trackIndex: Int) {
                    navController.openContextMenu(
                        mediaType = MediaType.TRACK,
                        mediaId = trackId,
                        mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                        trackIndex = trackIndex
                    )
                }

            }
        )
    }
}