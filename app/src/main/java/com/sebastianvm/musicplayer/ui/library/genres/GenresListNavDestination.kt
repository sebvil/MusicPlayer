package com.sebastianvm.musicplayer.ui.library.genres

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToGenre
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.genresListNavDestination(navController: NavController) {
    composable(NavRoutes.GENRES_ROOT) {
        val screenViewModel = hiltViewModel<GenresListViewModel>()
        GenresListScreen(screenViewModel) { genre ->
            navController.navigateToGenre(genre)
        }
    }
}