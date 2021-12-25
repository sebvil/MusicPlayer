package com.sebastianvm.musicplayer.ui.library.artists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

fun NavGraphBuilder.artistsNavDestination(navController: NavController) {
    composable(NavRoutes.ARTISTS_ROOT) {
        val screenViewModel = hiltViewModel<ArtistsListViewModel>()
        ArtistsListScreen(screenViewModel, object : ArtistsListScreenNavigationDelegate {
            override fun navigateToArtist(artistId: String) {
                navController.navigateToArtist(artistId)
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun openContextMenu(artistId: String) {
                navController.openContextMenu(
                    MediaType.ARTIST,
                    artistId,
                    MediaGroup(MediaType.ARTIST, mediaId = artistId),
                    SortOption.TRACK_NAME,
                    SortOrder.ASCENDING
                )
            }
        })
    }

}