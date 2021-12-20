package com.sebastianvm.musicplayer.ui.artist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo


fun NavGraphBuilder.artistNavDestination(navController: NavController) {
    composable(
        createNavRoute(NavRoutes.ARTIST, NavArgs.ARTIST_GID),
    ) {
        val screenViewModel = hiltViewModel<ArtistViewModel>()
        ArtistScreen(screenViewModel) { albumGid ->
            navController.navigateToAlbum(albumGid)
        }
    }
}

fun NavController.navigateToArtist(artistId: String) {
    navigateTo(NavRoutes.ARTIST, NavArgument(NavArgs.ARTIST_GID, artistId))
}