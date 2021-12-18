package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.sebastianvm.musicplayer.ui.album.albumNavDestination
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.sortBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.library.albums.albumsListNavDestination
import com.sebastianvm.musicplayer.ui.library.artists.artistsNavDestination
import com.sebastianvm.musicplayer.ui.library.genres.genresListNavDestination
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreen
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreenActivityDelegate
import com.sebastianvm.musicplayer.ui.library.root.LibraryViewModel
import com.sebastianvm.musicplayer.ui.library.tracks.tracksListNavDestination
import com.sebastianvm.musicplayer.ui.player.MusicPlayerScreen
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewModel
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    requestPermission: (String) -> String,
) {
    val bottomNavBar = @Composable { BottomNavBar(navController = navController) }
    Scaffold(bottomBar = bottomNavBar) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LIBRARY,
            modifier = Modifier.padding(paddingValues)
        ) {

            libraryGraph(
                navController = navController,
                requestPermission = requestPermission,
            )

            composable(NavRoutes.PLAYER) {
                val screenViewModel = hiltViewModel<MusicPlayerViewModel>()
                MusicPlayerScreen(screenViewModel)
            }

            composable(NavRoutes.SEARCH) {
                val screenViewModel = hiltViewModel<SearchViewModel>()
                SearchScreen(screenViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.libraryGraph(
    navController: NavHostController,
    requestPermission: (String) -> String,
) {

    navigation(startDestination = NavRoutes.LIBRARY_ROOT, route = NavRoutes.LIBRARY) {
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

        tracksListNavDestination(navController)
        artistsNavDestination(navController)
        albumsListNavDestination(navController)
        genresListNavDestination(navController)

        artistNavDestination(navController)
        albumNavDestination(navController)

        sortBottomSheetNavDestination(navController)
        contextBottomSheet(navController)
    }
}