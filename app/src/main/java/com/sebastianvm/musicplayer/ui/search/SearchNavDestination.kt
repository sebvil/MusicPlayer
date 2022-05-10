package com.sebastianvm.musicplayer.ui.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToGenre
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.player.navigateToPlayer

fun NavGraphBuilder.searchNavDestination(navController: NavController) {
    composable(NavRoutes.SEARCH) {
        val screenViewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(screenViewModel, delegate = object : SearchNavigationDelegate {
            override fun navigateToPlayer() {
                navController.navigateToPlayer()
            }

            override fun navigateToArtist(artistId: Long) {
                navController.navigateToArtist(artistId)
            }

            override fun navigateToAlbum(albumId: Long) {
                navController.navigateToAlbum(albumId)
            }

            override fun navigateToGenre(genreId: Long) {
                navController.navigateToGenre(genreId)
            }

            override fun openContextMenu(mediaType: MediaType, mediaGroup: MediaGroup) {
                navController.openContextMenu(
                    mediaType = mediaType,
                    mediaId = mediaGroup.mediaId,
                    mediaGroup = mediaGroup,
                )
            }
        })
    }
}
