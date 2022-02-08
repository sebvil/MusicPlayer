package com.sebastianvm.musicplayer.ui.library.artists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

fun NavGraphBuilder.artistsNavDestination(navController: NavController) {
    composable(NavRoutes.ARTISTS_ROOT) {
        val screenViewModel = hiltViewModel<ArtistsListViewModel>()
        ArtistsListScreen(screenViewModel, object : ArtistsListScreenNavigationDelegate {
            override fun navigateToArtist(artistName: String) {
                navController.navigateToArtist(artistName)
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun openContextMenu(artistName: String) {
                navController.openContextMenu(
                    MediaType.ARTIST,
                    artistName,
                    MediaGroup(MediaGroupType.ARTIST, mediaId = artistName),
                )
            }
        })
    }

}
