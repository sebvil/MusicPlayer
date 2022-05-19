package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.albumListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<AlbumListViewModel>(NavigationRoute.AlbumsRoot) { viewModel ->
        AlbumListScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            object : AlbumListScreenNavigationDelegate {
                override fun openSortMenu() {
                    navController.openSortBottomSheet(listType = SortableListType.ALBUMS)
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