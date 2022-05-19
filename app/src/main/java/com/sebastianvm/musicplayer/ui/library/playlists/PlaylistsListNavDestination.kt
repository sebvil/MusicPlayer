package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.playlistsListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<PlaylistsListViewModel>(NavigationRoute.PlaylistsRoot) { viewModel ->
        PlaylistsListScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            object : PlaylistsListScreenNavigationDelegate {
                override fun openContextMenu(playlistId: Long) {
                    navController.openContextMenu(
                        mediaType = MediaType.PLAYLIST,
                        mediaId = playlistId,
                        mediaGroup = MediaGroup(MediaGroupType.PLAYLIST, playlistId),
                    )
                }
            })
    }
}