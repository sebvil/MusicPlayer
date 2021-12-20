package com.sebastianvm.musicplayer.ui.library.root

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes


fun NavGraphBuilder.libraryNavDestination(navController: NavController, requestPermission: (permission: String) -> String) {
    composable(NavRoutes.LIBRARY_ROOT) {
        val screenViewModel = hiltViewModel<LibraryViewModel>()
        LibraryScreen(
            screenViewModel = screenViewModel,
            delegate = object : LibraryScreenActivityDelegate {
                override fun getPermissionStatus(permission: String): String {
                    return requestPermission(permission)
                }

                override fun navigateToLibraryScreen(route: String) {
                    navController.navigate(route = route)
                }

            })
    }
}