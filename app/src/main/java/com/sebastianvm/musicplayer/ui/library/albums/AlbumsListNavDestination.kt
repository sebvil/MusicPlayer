package com.sebastianvm.musicplayer.ui.library.albums

import android.support.v4.media.MediaMetadataCompat
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder


fun NavGraphBuilder.albumsListNavDestination(navController: NavController) {
    composable(NavRoutes.ALBUMS_ROOT) {
        val screenViewModel = hiltViewModel<AlbumsListViewModel>()
        val lifecycleOwner = LocalLifecycleOwner.current
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(NavArgs.SORT_OPTION)
            ?.observe(lifecycleOwner) {
                screenViewModel.handle(
                    AlbumsListUserAction.SortOptionClicked(SortOption.fromResId(it))
                )
            }
        AlbumsListScreen(screenViewModel, object : AlbumsListScreenNavigationDelegate {
            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun openSortMenu(sortOption: Int, sortOrder: SortOrder) {
                navController.openSortBottomSheet(NavRoutes.ALBUMS_ROOT, sortOption, sortOrder)
            }

            override fun navigateToAlbum(albumId: String) {
                navController.navigateToAlbum(albumId)
            }

            override fun openContextMenu(albumId: String) {
                navController.openContextMenu(
                    mediaType = MediaType.ALBUM.name,
                    mediaId = albumId,
                    mediaGroup = MediaGroup(MediaType.ALBUM, albumId),
                    currentSort = MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                    sortOrder = SortOrder.ASCENDING,
                )
            }
        })
    }
}