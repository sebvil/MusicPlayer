package com.sebastianvm.musicplayer.ui.library.tracks

import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.bottomsheets.context.openContextMenu
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.openSortBottomSheet
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo
import com.sebastianvm.musicplayer.ui.player.navigateToPlayer
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder

fun NavGraphBuilder.tracksListNavDestination(navController: NavController) {
    composable(
        createNavRoute(
            NavRoutes.TRACKS_ROOT,
            NavArgs.GENRE_NAME,
            NavArgs.SORT_OPTION,
            NavArgs.SORT_ORDER
        ),
        arguments = listOf(
            navArgument(NavArgs.GENRE_NAME) {
                nullable = true
                type = NavType.StringType
            },
            navArgument(NavArgs.SORT_OPTION) {
                nullable = true
                type = NavType.StringType
            },
        )
    ) {
        val screenViewModel = hiltViewModel<TracksListViewModel>()
        val lifecycleOwner = LocalLifecycleOwner.current
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(NavArgs.SORT_OPTION)
            ?.observe(lifecycleOwner) {
                screenViewModel.handle(
                    TracksListUserAction.SortOptionClicked(SortOption.fromResId(it))
                )
            }

        TracksListScreen(
            screenViewModel,
            object : TracksListScreenNavigationDelegate {
                override fun navigateToPlayer() {
                    navController.navigateToPlayer()
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }

                override fun openSortMenu(sortOption: Int, sortOrder: SortOrder) {
                    navController.openSortBottomSheet(NavRoutes.TRACKS_ROOT, sortOption, sortOrder)
                }

                override fun openContextMenu(
                    mediaId: String,
                    mediaGroup: MediaGroup,
                    currentSort: SortOption,
                    sortOrder: SortOrder
                ) {
                    navController.openContextMenu(
                        mediaType = MediaType.ALL_TRACKS,
                        mediaId = mediaId,
                        mediaGroup = mediaGroup,
                        currentSort = currentSort,
                        sortOrder = sortOrder,
                    )
                }
            }
        )
    }
}

fun NavController.navigateToGenre(genreName: String) {
    navigateTo(NavRoutes.TRACKS_ROOT, NavArgument(NavArgs.GENRE_NAME, genreName))
}