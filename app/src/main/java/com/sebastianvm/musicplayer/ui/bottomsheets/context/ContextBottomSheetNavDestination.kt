package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class ContextMenuArguments(
    val mediaId: Long,
    val mediaType: MediaType,
    val mediaGroup: MediaGroup? = null,
    val trackIndex: Int = 0
) : NavigationArguments

fun NavGraphBuilder.contextBottomSheet(
    navigationDelegate: NavigationDelegate,
) {
//    bottomSheet(
//        route = createNavRoute(
//            NavRoutes.CONTEXT,
//            NavArgs.MEDIA_ID,
//            NavArgs.MEDIA_TYPE,
//            NavArgs.MEDIA_GROUP_TYPE,
//            NavArgs.MEDIA_GROUP_ID,
//            NavArgs.TRACK_INDEX
//        ),
//        arguments = listOf(
//            navArgument(NavArgs.MEDIA_ID) { type = NavType.LongType },
//            navArgument(NavArgs.MEDIA_TYPE) { type = NavType.StringType },
//            navArgument(NavArgs.MEDIA_GROUP_TYPE) { type = NavType.StringType },
//            navArgument(NavArgs.MEDIA_GROUP_ID) {
//                type = NavType.LongType
//                defaultValue = -1
//            },
//            navArgument(NavArgs.TRACK_INDEX) {
//                type = NavType.IntType
//                defaultValue = 0
//            }
//        )
//    ) { backedStackEntry ->
//        val sheetViewModel =
//            when (MediaType.valueOf(backedStackEntry.arguments?.getString(NavArgs.MEDIA_TYPE)!!)) {
//                MediaType.TRACK -> hiltViewModel<TrackContextMenuViewModel>()
//                MediaType.ARTIST -> hiltViewModel<ArtistContextMenuViewModel>()
//                MediaType.GENRE -> hiltViewModel<GenreContextMenuViewModel>()
//                MediaType.ALBUM -> hiltViewModel<AlbumContextMenuViewModel>()
//                MediaType.PLAYLIST -> hiltViewModel<PlaylistContextMenuViewModel>()
//            }
//        ContextBottomSheet(
//            sheetViewModel = sheetViewModel,
//            delegate = object : ContextBottomSheetDialogNavigationDelegate {
//                override fun navigateToPlayer() {
//                    navController.navigate(NavRoutes.PLAYER) {
//                        navController.popBackStack()
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
//                        }
//                        // Avoid multiple copies of the same destination when
//                        // reselecting the same item
//                        launchSingleTop = true
//                        // Restore state when reselecting a previously selected item
//                        restoreState = true
//                    }
//                }
//
//                override fun navigateToAlbum(albumId: Long) {
////                    NavigationDelegate(navController).navigateToScreen(
////                        NavigationDestination.AlbumDestination(
////                            AlbumArguments(albumId)
////                        )
////                    )
//                }
//
//                override fun navigateToArtist(artistId: Long) {
////                    NavigationDelegate(navController).navigateToScreen(
////                        NavigationDestination.ArtistDestination(
////                            ArtistArguments(artistId = artistId)
////                        )
////                    )
//                }
//
//                override fun navigateToArtistsBottomSheet(mediaId: Long, mediaType: MediaType) {
////                    navController.popBackStack()
////                    navController.navigateToArtistsBottomSheet(mediaId, mediaType)
//                }
//
//                override fun navigateToGenre(genreId: Long) {
////                    navigationDelegate.navigateToScreen(
////                        NavigationDestination.TrackList(
////                            TrackListArguments(
////                                trackListId = genreId,
////                                trackListType = TrackListType.GENRE
////                            )
////                        )
////                    )
//                }
//
//                override fun navigateToPlaylist(playlistId: Long) {
////                    navController.navigateToPlaylist(playlistName)
//                }
//
//                override fun hideBottomSheet() {
//                    navController.popBackStack()
//                }
//            }
//        )
//    }
}
