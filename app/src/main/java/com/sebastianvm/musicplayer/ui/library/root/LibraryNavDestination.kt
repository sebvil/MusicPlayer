package com.sebastianvm.musicplayer.ui.library.root

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToTracksRoot
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes


fun NavGraphBuilder.libraryNavDestination(navController: NavController) {
    composable(NavRoutes.LIBRARY_ROOT) {
        val screenViewModel = hiltViewModel<LibraryViewModel>()
        LibraryScreen(
            screenViewModel = screenViewModel,
            delegate = object : LibraryScreenActivityDelegate {

                override fun navigateToLibraryScreen(route: String) {
                    if (route == NavRoutes.TRACKS_ROOT) {
                        navController.navigateToTracksRoot()
                        return
                    }
                    navController.navigate(route = route)
                }

            })
    }
}