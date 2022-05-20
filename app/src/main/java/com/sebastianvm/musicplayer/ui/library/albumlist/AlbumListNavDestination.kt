package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.albumListNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<AlbumListViewModel>(
        destination = NavigationRoute.AlbumsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        AlbumListScreen(
            viewModel,
            navigationDelegate = navigationDelegate,
            object : AlbumListScreenNavigationDelegate {
                override fun openSortMenu() {
                    navController.openSortBottomSheet(listType = SortableListType.ALBUMS)
                }

            })
    }
}