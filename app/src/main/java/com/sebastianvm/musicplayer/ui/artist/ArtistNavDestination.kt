package com.sebastianvm.musicplayer.ui.artist

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo


fun NavGraphBuilder.artistNavDestination(navController: NavController) {
    composable(
        createNavRoute(NavRoutes.ARTIST, NavArgs.ARTIST_ID),
    ) {
        val screenViewModel = hiltViewModel<ArtistViewModel>()
        ArtistScreen(screenViewModel, delegate = object : ArtistScreenNavigationDelegate {
            override fun navigateToAlbum(albumId: Long) {
                NavigationDelegate(navController).navigateToScreen(
                    NavigationDestination.AlbumDestination(
                        AlbumArguments(albumId)
                    )
                )
            }

            override fun openContextMenu(albumId: Long) {
                navController.openContextMenu(
                    mediaType = MediaType.ALBUM,
                    mediaId = albumId,
                    mediaGroup = MediaGroup(MediaGroupType.ALBUM, albumId),
                )
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

        })
    }
}

fun NavController.navigateToArtist(artistId: Long) {
    navigateTo(NavRoutes.ARTIST, NavArgument(NavArgs.ARTIST_ID, artistId))
}
