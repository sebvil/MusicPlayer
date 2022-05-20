package com.sebastianvm.musicplayer.ui.search

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate

fun NavGraphBuilder.searchNavDestination(
    navigationDelegate: NavigationDelegate, navController: NavController
) {
    composable(NavRoutes.SEARCH) {
        val screenViewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(screenViewModel, delegate = object : SearchNavigationDelegate {
            override fun navigateToPlayer() {
//                NavigationDelegate(navController).navigateToScreen(
//                    NavigationDestination.MusicPlayer
//                )
            }

            override fun navigateToArtist(artistId: Long) {
//                NavigationDelegate(navController).navigateToScreen(
//                    NavigationDestination.ArtistDestination(
//                        ArtistArguments(artistId = artistId)
//                    )
//                )
            }

            override fun navigateToAlbum(albumId: Long) {
//                NavigationDelegate(navController).navigateToScreen(
//                    NavigationDestination.AlbumDestination(
//                        AlbumArguments(albumId)
//                    )
//                )
            }

            override fun navigateToGenre(genreId: Long) {
//                navigationDelegate.navigateToScreen(
//                    NavigationDestination.TrackList(
//                        TrackListArguments(
//                            trackListId = genreId, trackListType = TrackListType.GENRE
//                        )
//                    )
//                )
            }

            override fun openContextMenu(mediaType: MediaType, mediaGroup: MediaGroup) {
//                navController.openContextMenu(
//                    mediaType = mediaType,
//                    mediaId = mediaGroup.mediaId,
//                    mediaGroup = mediaGroup,
//                )
            }
        })
    }
}
