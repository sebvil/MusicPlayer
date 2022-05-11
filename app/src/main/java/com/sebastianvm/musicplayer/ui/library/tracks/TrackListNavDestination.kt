package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.ui.player.navigateToPlayer

fun NavGraphBuilder.trackListNavDestination(navController: NavController) {
    composable(
        createNavRoute(
            NavRoutes.TRACKS_ROOT,
            NavArgs.TRACK_LIST_ID,
            NavArgs.TRACKS_LIST_TYPE
        ),
        arguments = listOf(
            navArgument(NavArgs.TRACK_LIST_ID) {
                nullable = true
                type = NavType.StringType
            },
            navArgument(NavArgs.TRACKS_LIST_TYPE) {
                type = NavType.StringType
            },
        )
    ) {
        val screenViewModel = hiltViewModel<TrackListViewModel>()
        TrackListScreen(
            screenViewModel,
            object : TrackListScreenNavigationDelegate {
                override fun navigateToPlayer() {
                    navController.navigateToPlayer()
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }

                override fun openSortMenu(mediaId: Long) {
                    navController.openSortBottomSheet(listType = SortableListType.TRACKS, mediaId = mediaId)
                }

                override fun openContextMenu(mediaId: Long, mediaGroup: MediaGroup, trackIndex: Int) {
                    navController.openContextMenu(
                        mediaType = MediaType.TRACK,
                        mediaId = mediaId,
                        mediaGroup = mediaGroup,
                        trackIndex = trackIndex
                    )
                }
            }
        )
    }
}

fun NavController.navigateToTracksRoot() {
    navigateTo(
        NavRoutes.TRACKS_ROOT,
        NavArgument(NavArgs.TRACKS_LIST_TYPE, TrackListType.ALL_TRACKS)
    )
}

fun NavController.navigateToGenre(genreId: Long) {
    navigateTo(
        NavRoutes.TRACKS_ROOT,
        NavArgument(NavArgs.TRACK_LIST_ID, genreId),
        NavArgument(NavArgs.TRACKS_LIST_TYPE, TrackListType.GENRE)
    )
}