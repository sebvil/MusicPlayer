package com.sebastianvm.musicplayer.ui.library.artists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.artistsNavDestination(navController: NavController) {
    composable(NavRoutes.ARTISTS_ROOT) {
        val screenViewModel = hiltViewModel<ArtistsListViewModel>()
        ArtistsListScreen(screenViewModel) { artistGid ->
            navController.navigateToArtist(artistGid)
        }
    }

}