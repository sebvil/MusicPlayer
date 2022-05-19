package com.sebastianvm.musicplayer.ui.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination

fun NavGraphBuilder.searchNavDestination(
    navigationDelegate: NavigationDelegate, navController: NavController
) {
    composable(NavRoutes.SEARCH) {
        val screenViewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(screenViewModel, delegate = object : SearchNavigationDelegate {
            override fun navigateToPlayer() {
                NavigationDelegate(navController).navigateToScreen(
                    NavigationDestination.MusicPlayerDestination
                )
            }

            override fun navigateToArtist(artistId: Long) {
                navController.navigateToArtist(artistId)
            }

            override fun navigateToAlbum(albumId: Long) {
                NavigationDelegate(navController).navigateToScreen(
                    NavigationDestination.AlbumDestination(
                        AlbumArguments(albumId)
                    )
                )
            }

            override fun navigateToGenre(genreId: Long) {
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackListDestination(
                        TrackListArguments(
                            trackListId = genreId, trackListType = TrackListType.GENRE
                        )
                    )
                )
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
