package com.sebastianvm.musicplayer.ui.library.artists

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.artistsNavDestination(navController: NavController, bottomNavBar: @Composable () -> Unit) {
    composable(NavRoutes.ARTISTS_ROOT) {
        val screenViewModel = hiltViewModel<ArtistsListViewModel>()
        ArtistsListScreen(screenViewModel, bottomNavBar) { artistGid ->
            navController.navigateToArtist(artistGid)
        }
    }

}