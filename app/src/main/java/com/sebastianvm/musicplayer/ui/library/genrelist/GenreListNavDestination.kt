package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToGenre
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.genreListNavDestination(navController: NavController) {
    composable(NavRoutes.GENRES_ROOT) {
        val screenViewModel = hiltViewModel<GenreListViewModel>()
        GenreListScreen(screenViewModel, object : GenreListScreenNavigationDelegate {
            override fun navigateToGenre(genreId: Long) {
                navController.navigateToGenre(genreId)
            }

            override fun openContextMenu(genreId: Long) {
                navController.openContextMenu(
                    mediaType = MediaType.GENRE,
                    mediaId = genreId,
                    mediaGroup = MediaGroup(MediaGroupType.GENRE, genreId),
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }
        })
    }
}
