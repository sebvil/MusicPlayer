package com.sebastianvm.musicplayer.ui.library.genres

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
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

fun NavGraphBuilder.genresListNavDestination(navController: NavController) {
    composable(NavRoutes.GENRES_ROOT) {
        val screenViewModel = hiltViewModel<GenresListViewModel>()
        GenresListScreen(screenViewModel, object : GenresListScreenNavigationDelegate {
            override fun navigateToGenre(genreName: String) {
                navController.navigateToGenre(genreName)
            }

            override fun openContextMenu(
                genreName: String,
                currentSort: MediaSortOption,
                sortOrder: MediaSortOrder
            ) {
                navController.openContextMenu(
                    mediaType = MediaType.GENRE,
                    mediaId = genreName,
                    mediaGroup = MediaGroup(MediaGroupType.GENRE, genreName),
                    currentSort = currentSort,
                    sortOrder = sortOrder,
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }
        })
    }
}
