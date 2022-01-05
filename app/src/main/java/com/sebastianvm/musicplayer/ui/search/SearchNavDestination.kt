package com.sebastianvm.musicplayer.ui.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToGenre
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.player.navigateToPlayer
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

fun NavGraphBuilder.searchNavDestination(navController: NavController) {
    composable(NavRoutes.SEARCH) {
        val screenViewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(screenViewModel, delegate = object : SearchNavigationDelegate {
            override fun navigateToPlayer() {
                navController.navigateToPlayer()
            }

            override fun navigateToArtist(artistId: String) {
                navController.navigateToArtist(artistId)
            }

            override fun navigateToAlbum(albumId: String) {
                navController.navigateToAlbum(albumId)
            }

            override fun navigateToGenre(genreName: String) {
                navController.navigateToGenre(genreName)
            }

            override fun openContextMenu(
                mediaGroup: MediaGroup,
                sortOption: SortOption,
                sortOrder: SortOrder
            ) {
                navController.openContextMenu(
                    mediaType = mediaGroup.mediaType,
                    mediaId = mediaGroup.mediaId,
                    mediaGroup = mediaGroup,
                    currentSort = sortOption,
                    sortOrder = sortOrder,
                )
            }
        })
    }
}