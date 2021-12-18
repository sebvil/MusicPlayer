package com.sebastianvm.musicplayer.ui.library.albums

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes


fun NavGraphBuilder.albumsListNavDestination(navController: NavController) {
    composable(NavRoutes.ALBUMS_ROOT) {
        val screenViewModel = hiltViewModel<AlbumsListViewModel>()
        AlbumsListScreen(screenViewModel) { albumGid ->
            navController.navigateToAlbum(albumGid)
        }
    }
}