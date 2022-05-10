package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.navigateToArtistsBottomSheet
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToGenre
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.contextBottomSheet(navController: NavController) {
    bottomSheet(
        route = createNavRoute(
            NavRoutes.CONTEXT,
            NavArgs.MEDIA_ID,
            NavArgs.MEDIA_TYPE,
            NavArgs.MEDIA_GROUP_TYPE,
            NavArgs.MEDIA_GROUP_ID,
            NavArgs.TRACK_INDEX
        ),
        arguments = listOf(
            navArgument(NavArgs.MEDIA_ID) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_TYPE) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_GROUP_TYPE) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_GROUP_ID) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(NavArgs.TRACK_INDEX) {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backedStackEntry ->
        val sheetViewModel =
            when (MediaType.valueOf(backedStackEntry.arguments?.getString(NavArgs.MEDIA_TYPE)!!)) {
                MediaType.TRACK -> hiltViewModel<TrackContextMenuViewModel>()
                MediaType.ARTIST -> hiltViewModel<ArtistContextMenuViewModel>()
                MediaType.GENRE -> hiltViewModel<GenreContextMenuViewModel>()
                MediaType.ALBUM -> hiltViewModel<AlbumContextMenuViewModel>()
                MediaType.PLAYLIST -> hiltViewModel<PlaylistContextMenuViewModel>()
            }
        ContextBottomSheet(
            sheetViewModel = sheetViewModel,
            delegate = object : ContextBottomSheetDialogNavigationDelegate {
                override fun navigateToPlayer() {
                    navController.navigate(NavRoutes.PLAYER) {
                        navController.popBackStack()
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }

                override fun navigateToAlbum(albumId: Long) {
                    navController.navigateToAlbum(albumId)
                }

                override fun navigateToArtist(artistId: Long) {
                    navController.navigateToArtist(artistId)
                }

                override fun navigateToArtistsBottomSheet(mediaId: Long, mediaType: MediaType) {
                    navController.popBackStack()
                    navController.navigateToArtistsBottomSheet(mediaId, mediaType)
                }

                override fun navigateToGenre(genreId: Long) {
                    navController.navigateToGenre(genreId)
                }

                override fun navigateToPlaylist(playlistId: Long) {
//                    navController.navigateToPlaylist(playlistName)
                }

                override fun hideBottomSheet() {
                    navController.popBackStack()
                }
            }
        )
    }
}

fun NavController.openContextMenu(
    mediaType: MediaType,
    mediaId: Long,
    mediaGroup: MediaGroup,
    trackIndex: Int? = null
) {
    navigateTo(
        NavRoutes.CONTEXT,
        NavArgument(NavArgs.MEDIA_ID, mediaId),
        NavArgument(NavArgs.MEDIA_TYPE, mediaType.name),
        NavArgument(NavArgs.MEDIA_GROUP_TYPE, mediaGroup.mediaGroupType),
        NavArgument(NavArgs.MEDIA_GROUP_ID, mediaGroup.mediaId),
        NavArgument(NavArgs.TRACK_INDEX, trackIndex),
    )
}
