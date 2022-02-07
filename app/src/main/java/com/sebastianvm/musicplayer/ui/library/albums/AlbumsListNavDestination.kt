package com.sebastianvm.musicplayer.ui.library.albums

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.mediaSortOptionFromResId
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder


fun NavGraphBuilder.albumsListNavDestination(navController: NavController) {
    composable(NavRoutes.ALBUMS_ROOT) {
        val screenViewModel = hiltViewModel<AlbumsListViewModel>()
        val lifecycleOwner = LocalLifecycleOwner.current
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(NavArgs.SORT_OPTION)
            ?.observe(lifecycleOwner) {
                screenViewModel.handle(
                    AlbumsListUserAction.MediaSortOptionClicked(mediaSortOptionFromResId(it))
                )
            }
        AlbumsListScreen(screenViewModel, object : AlbumsListScreenNavigationDelegate {
            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun openSortMenu(sortOption: Int, sortOrder: MediaSortOrder) {
                navController.openSortBottomSheet(NavRoutes.ALBUMS_ROOT, sortOption, sortOrder)
            }

            override fun navigateToAlbum(albumId: String) {
                navController.navigateToAlbum(albumId)
            }

            override fun openContextMenu(albumId: String) {
                navController.openContextMenu(
                    mediaType = MediaType.ALBUM,
                    mediaId = albumId,
                    mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                    currentSort = MediaSortOption.TRACK_NUMBER,
                    sortOrder = MediaSortOrder.ASCENDING,
                )
            }
        })
    }
}