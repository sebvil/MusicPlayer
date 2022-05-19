package com.sebastianvm.musicplayer.ui.library.root

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.library.tracks.navigateToTracksRoot
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.libraryNavDestination(
    navigationDelegate: NavigationDelegate,
    navController: NavController
) {
    screenDestination<LibraryViewModel>(NavigationRoute.LibraryRoot) { viewModel ->
        LibraryScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
            delegate = object : LibraryScreenNavigationDelegate {

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