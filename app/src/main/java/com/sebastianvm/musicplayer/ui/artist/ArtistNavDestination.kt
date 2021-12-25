package com.sebastianvm.musicplayer.ui.artist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder


fun NavGraphBuilder.artistNavDestination(navController: NavController) {
    composable(
        createNavRoute(NavRoutes.ARTIST, NavArgs.ARTIST_ID),
    ) {
        val screenViewModel = hiltViewModel<ArtistViewModel>()
        ArtistScreen(screenViewModel, delegate = object : ArtistScreenNavigationDelegate {
            override fun navigateToAlbum(albumId: String) {
                navController.navigateToAlbum(albumId)
            }

            override fun openContextMenu(albumId: String) {
                navController.openContextMenu(
                    mediaType = MediaType.ALBUM,
                    mediaId = albumId,
                    mediaGroup = MediaGroup(MediaType.ALBUM, albumId),
                    currentSort = SortOption.TRACK_NUMBER,
                    sortOrder = SortOrder.ASCENDING,
                )
            }

        })
    }
}

fun NavController.navigateToArtist(artistId: String) {
    navigateTo(NavRoutes.ARTIST, NavArgument(NavArgs.ARTIST_ID, artistId))
}