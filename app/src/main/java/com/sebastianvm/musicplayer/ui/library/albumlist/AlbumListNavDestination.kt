package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes


fun NavGraphBuilder.albumListNavDestination(navController: NavController) {
    composable(NavRoutes.ALBUMS_ROOT) {
        val screenViewModel = hiltViewModel<AlbumListViewModel>()
        AlbumListScreen(screenViewModel, object : AlbumListScreenNavigationDelegate {
            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun openSortMenu() {
                navController.openSortBottomSheet(listType = SortableListType.ALBUMS)
            }

            override fun navigateToAlbum(albumId: Long) {
                navController.navigateToAlbum(albumId)
            }

            override fun openContextMenu(albumId: Long) {
                navController.openContextMenu(
                    mediaType = MediaType.ALBUM,
                    mediaId = albumId,
                    mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                )
            }
        })
    }
}