package com.sebastianvm.musicplayer.ui.album

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.navigation.NavArgument
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.createNavRoute
import com.sebastianvm.musicplayer.ui.navigation.navigateTo

fun NavGraphBuilder.albumNavDestination(navController: NavController, bottomNavBar: @Composable () -> Unit) {
    composable(
        createNavRoute(NavRoutes.ALBUM, NavArgs.ALBUM_GID),
    ) {
        val screenViewModel = hiltViewModel<AlbumViewModel>()
        AlbumScreen(screenViewModel, bottomNavBar) {
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
    }
}

fun NavController.navigateToAlbum(albumId: String) {
    navigateTo(NavRoutes.ALBUM, NavArgument(NavArgs.ALBUM_GID, albumId))
}