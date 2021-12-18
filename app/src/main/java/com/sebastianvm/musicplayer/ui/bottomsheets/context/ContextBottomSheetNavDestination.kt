package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
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
            NavArgs.SCREEN,
            NavArgs.MEDIA_ID,
            NavArgs.SORT_OPTION,
            NavArgs.SORT_ORDER
        ),
        arguments = listOf(
            navArgument(NavArgs.SCREEN) { type = NavType.StringType },
            navArgument(NavArgs.MEDIA_ID) { type = NavType.StringType },
            navArgument(NavArgs.SORT_OPTION) { type = NavType.StringType },
            navArgument(NavArgs.SORT_ORDER) { type = NavType.StringType },
        )
    ) {
        val sheetViewModel: ContextMenuViewModel = hiltViewModel()
        ContextBottomSheet(
            sheetViewModel = sheetViewModel,
            delegate = object : ContextBottomSheetDialogNavigationDelegate {
                override fun navigateToPlayer() {
                    navController.navigate(NavRoutes.PLAYER) {
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

                override fun navigateToAlbum(albumGid: String, albumName: String) {
                    navController.navigateTo(
                        NavRoutes.ALBUM,
                        NavArgument(NavArgs.ALBUM_GID, albumGid),
                        NavArgument(NavArgs.ALBUM_NAME, albumName)
                    )
                }
            }
        )
    }
}
