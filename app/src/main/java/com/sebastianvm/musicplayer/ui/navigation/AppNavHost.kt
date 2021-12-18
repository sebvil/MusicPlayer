package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.ui.album.albumNavDestination
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheetDelegate
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheetViewModel
import com.sebastianvm.musicplayer.ui.library.albums.albumsListNavDestination
import com.sebastianvm.musicplayer.ui.library.artists.artistsNavDestination
import com.sebastianvm.musicplayer.ui.library.genres.GenresListScreen
import com.sebastianvm.musicplayer.ui.library.genres.GenresListViewModel
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreen
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreenActivityDelegate
import com.sebastianvm.musicplayer.ui.library.root.LibraryViewModel
import com.sebastianvm.musicplayer.ui.library.tracks.tracksListNavDestination
import com.sebastianvm.musicplayer.ui.player.MusicPlayerScreen
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewModel
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.search.SearchViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    requestPermission: (String) -> String,
) {
    val bottomNavBar = @Composable { BottomNavBar(navController = navController) }
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LIBRARY,
    ) {

        libraryGraph(
            navController = navController,
            bottomNavBar = bottomNavBar,
            requestPermission = requestPermission,
        )

        composable(NavRoutes.PLAYER) {
            val screenViewModel = hiltViewModel<MusicPlayerViewModel>()
            MusicPlayerScreen(screenViewModel, bottomNavBar)
        }

        composable(NavRoutes.SEARCH) {
            val screenViewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(screenViewModel, bottomNavBar)
        }
    }

}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.libraryGraph(
    navController: NavHostController,
    bottomNavBar: @Composable () -> Unit,
    requestPermission: (String) -> String,
) {

    navigation(startDestination = NavRoutes.LIBRARY_ROOT, route = NavRoutes.LIBRARY) {
        composable(NavRoutes.LIBRARY_ROOT) {
            val screenViewModel = hiltViewModel<LibraryViewModel>()
            LibraryScreen(
                screenViewModel = screenViewModel,
                bottomNavBar = bottomNavBar,
                delegate = object : LibraryScreenActivityDelegate {
                    override fun getPermissionStatus(permission: String): String {
                        return requestPermission(permission)
                    }

                    override fun navigateToLibraryScreen(route: String) {
                        navController.navigate(route = route)
                    }

                })
        }

        tracksListNavDestination(navController, bottomNavBar)
        artistsNavDestination(navController, bottomNavBar)
        albumsListNavDestination(navController, bottomNavBar)

        composable(NavRoutes.GENRES_ROOT) {
            val screenViewModel = hiltViewModel<GenresListViewModel>()
            GenresListScreen(screenViewModel, bottomNavBar) { genre ->
                navController.navigateTo(
                    NavRoutes.TRACKS_ROOT,
                    NavArgument(NavArgs.GENRE_NAME, genre)
                )
            }
        }

        artistNavDestination(navController, bottomNavBar)
        albumNavDestination(navController, bottomNavBar)

        bottomSheet(
            route = createNavRoute(
                NavRoutes.SORT,
                NavArgs.SCREEN,
                NavArgs.SORT_OPTION,
                NavArgs.SORT_ORDER
            ),
            arguments = listOf(
                navArgument(NavArgs.SCREEN) { type = NavType.StringType },
                navArgument(NavArgs.SORT_OPTION) { type = NavType.ReferenceType },
                navArgument(NavArgs.SORT_ORDER) {
                    type = NavType.StringType
                },
            )
        ) {
            val sheetViewModel = hiltViewModel<SortBottomSheetViewModel>()
            SortBottomSheet(
                sheetViewModel = sheetViewModel,
                delegate = object : SortBottomSheetDelegate {
                    override fun popBackStack(sortOption: Int) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            NavArgs.SORT_OPTION,
                            sortOption
                        )
                        navController.popBackStack()
                    }
                })
        }
        contextBottomSheet(navController)
    }
}