package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.ui.player.navigateToPlayer

fun NavGraphBuilder.tracksListNavDestination(navController: NavController) {
    composable(
        createNavRoute(
            NavRoutes.TRACKS_ROOT,
            NavArgs.TRACK_LIST_NAME,
            NavArgs.TRACKS_LIST_TYPE
        ),
        arguments = listOf(
            navArgument(NavArgs.TRACK_LIST_NAME) {
                nullable = true
                type = NavType.StringType
            },
            navArgument(NavArgs.TRACKS_LIST_TYPE) {
                type = NavType.StringType
            },
        )
    ) {
        val screenViewModel = hiltViewModel<TracksListViewModel>()
        TracksListScreen(
            screenViewModel,
            object : TracksListScreenNavigationDelegate {
                override fun navigateToPlayer() {
                    navController.navigateToPlayer()
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }

                override fun openSortMenu() {
                    navController.openSortBottomSheet(NavRoutes.TRACKS_ROOT)
                }

                override fun openContextMenu(mediaId: String, mediaGroup: MediaGroup) {
                    navController.openContextMenu(
                        mediaType = MediaType.TRACK,
                        mediaId = mediaId,
                        mediaGroup = mediaGroup,
                    )
                }
            }
        )
    }
}

fun NavController.navigateToTracksRoot() {
    navigateTo(
        NavRoutes.TRACKS_ROOT,
        NavArgument(NavArgs.TRACKS_LIST_TYPE, TracksListType.ALL_TRACKS)
    )
}

fun NavController.navigateToGenre(genreName: String) {
    navigateTo(
        NavRoutes.TRACKS_ROOT,
        NavArgument(NavArgs.TRACK_LIST_NAME, genreName),
        NavArgument(NavArgs.TRACKS_LIST_TYPE, TracksListType.GENRE)
    )
}