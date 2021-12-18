package com.sebastianvm.musicplayer.ui.library.albums

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes


fun NavGraphBuilder.albumsListNavDestination(navController: NavController, bottomNavBar: @Composable () -> Unit) {
    composable(NavRoutes.ALBUMS_ROOT) {
        val screenViewModel = hiltViewModel<AlbumsListViewModel>()
        AlbumsListScreen(screenViewModel, bottomNavBar) { albumGid ->
            navController.navigateToAlbum(albumGid)
        }
    }
}