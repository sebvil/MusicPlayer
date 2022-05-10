package com.sebastianvm.musicplayer.ui.album

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo

fun NavGraphBuilder.albumNavDestination(navController: NavController) {
    composable(
        createNavRoute(NavRoutes.ALBUM, NavArgs.ALBUM_ID),
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

            override fun openContextMenu(trackId: String, albumId: String, trackIndex: Int) {
                navController.openContextMenu(
                    mediaType = MediaType.TRACK,
                    mediaId = trackId,
                    mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                    trackIndex = trackIndex
                )
            }
        })
    }
}

fun NavController.navigateToAlbum(albumId: Long) {
    navigateTo(NavRoutes.ALBUM, NavArgument(NavArgs.ALBUM_ID, albumId))
}