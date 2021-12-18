package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.ui.album.navigateToAlbum
import com.sebastianvm.musicplayer.ui.artist.navigateToArtist
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute

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

                override fun navigateToAlbum(albumGid: String) {
                    navController.navigateToAlbum(albumGid)
                }

                override fun navigateToArtist(artistGid: String) {
                    navController.navigateToArtist(artistGid)
                }
            }
        )
    }
}
