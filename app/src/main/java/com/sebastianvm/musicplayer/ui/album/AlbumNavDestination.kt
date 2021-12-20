package com.sebastianvm.musicplayer.ui.album

import android.support.v4.media.MediaMetadataCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.util.SortOrder

fun NavGraphBuilder.albumNavDestination(navController: NavController) {
    composable(
        createNavRoute(NavRoutes.ALBUM, NavArgs.ALBUM_GID),
    ) {
        val screenViewModel = hiltViewModel<AlbumViewModel>()
        AlbumScreen(screenViewModel, object : AlbumNavigationDelegate {
            override fun navigateToPlayer() {

                navController.navigate(NavRoutes.PLAYER) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            }

            override fun openContextMenu(trackId: String, albumId: String) {
                navController.openContextMenu(
                    mediaType = MediaType.TRACK.name,
                    mediaId = trackId,
                    mediaGroup = MediaGroup(MediaType.ALBUM, albumId),
                    currentSort = MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                    sortOrder = SortOrder.ASCENDING,
                )
            }
        })
    }
}

fun NavController.navigateToAlbum(albumId: String) {
    navigateTo(NavRoutes.ALBUM, NavArgument(NavArgs.ALBUM_GID, albumId))
}