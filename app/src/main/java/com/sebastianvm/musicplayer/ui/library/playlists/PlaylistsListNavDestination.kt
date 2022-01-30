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
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

fun NavGraphBuilder.playlistsListNavDestination(navController: NavController) {
    composable(NavRoutes.PLAYLISTS_ROOT) {
        val screenViewModel = hiltViewModel<PlaylistsListViewModel>()
        PlaylistsListScreen(screenViewModel, object : PlaylistsListScreenNavigationDelegate {
            override fun navigateToPlaylist(playlistName: String) = Unit

            override fun openContextMenu(
                playlistName: String,
                currentSort: SortOption,
                sortOrder: SortOrder
            ) {
                navController.openContextMenu(
                    mediaType = MediaType.PLAYLIST,
                    mediaId = playlistName,
                    mediaGroup = MediaGroup(MediaGroupType.PLAYLIST, playlistName),
                    currentSort = currentSort,
                    sortOrder = sortOrder,
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }
        })
    }
}