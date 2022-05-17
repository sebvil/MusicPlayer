package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.playlist.PlaylistArguments
import com.sebastianvm.musicplayer.ui.playlist.navigateToPlaylist

fun NavGraphBuilder.playlistsListNavDestination(navController: NavController) {
    composable(NavRoutes.PLAYLISTS_ROOT) {
        val screenViewModel = hiltViewModel<PlaylistsListViewModel>()
        PlaylistsListScreen(screenViewModel, object : PlaylistsListScreenNavigationDelegate {
            override fun navigateToPlaylist(playlistId: Long) {
                navController.navigateToPlaylist(PlaylistArguments(playlistId = playlistId))
            }

            override fun openContextMenu(playlistId: Long) {
                navController.openContextMenu(
                    mediaType = MediaType.PLAYLIST,
                    mediaId = playlistId,
                    mediaGroup = MediaGroup(MediaGroupType.PLAYLIST, playlistId),
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }
        })
    }
}