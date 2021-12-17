package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.sebastianvm.musicplayer.ui.album.AlbumScreen
import com.sebastianvm.musicplayer.ui.album.AlbumViewModel
import com.sebastianvm.musicplayer.ui.artist.ArtistScreen
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheetDelegate
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortBottomSheetViewModel
import com.sebastianvm.musicplayer.ui.library.albums.AlbumsListScreen
import com.sebastianvm.musicplayer.ui.library.albums.AlbumsListViewModel
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsListScreen
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsListViewModel
import com.sebastianvm.musicplayer.ui.library.genres.GenresListScreen
import com.sebastianvm.musicplayer.ui.library.genres.GenresListViewModel
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreen
import com.sebastianvm.musicplayer.ui.library.root.LibraryScreenActivityDelegate
import com.sebastianvm.musicplayer.ui.library.root.LibraryViewModel
import com.sebastianvm.musicplayer.ui.library.tracks.SortOption
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListScreen
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListScreenNavigationDelegate
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListUserAction
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListViewModel
import com.sebastianvm.musicplayer.ui.player.MusicPlayerScreen
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewModel
import com.sebastianvm.musicplayer.ui.search.SearchScreen
import com.sebastianvm.musicplayer.ui.search.SearchViewModel
import com.sebastianvm.musicplayer.util.SortOrder

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
                        TracksListUserAction.SortOptionClicked(
                            SortOption.fromResId(
                                it
                            )
                        )
                    )
                }

            TracksListScreen(
                screenViewModel,
                bottomNavBar,
                object : TracksListScreenNavigationDelegate {
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

                    override fun navigateUp() {
                        navController.navigateUp()
                    }

                    override fun openSortMenu(sortOption: Int, sortOrder: SortOrder) {
                        navController.navigateTo(
                            NavRoutes.SORT,
                            NavArgument(NavArgs.SCREEN, NavRoutes.TRACKS_ROOT),
                            NavArgument(NavArgs.SORT_OPTION, sortOption),
                            NavArgument(NavArgs.SORT_ORDER, sortOrder.name)
                        )
                    }

                    override fun openContextMenu() {
                        navController.navigateTo(
                            NavRoutes.CONTEXT,
                            NavArgument(NavArgs.SCREEN, NavRoutes.TRACKS_ROOT),
                        )
                    }
                })
        }

        composable(NavRoutes.ARTISTS_ROOT) {
            val screenViewModel = hiltViewModel<ArtistsListViewModel>()
            ArtistsListScreen(screenViewModel, bottomNavBar) { artistGid, artistName ->
                navController.navigateTo(
                    NavRoutes.ARTIST,
                    NavArgument(NavArgs.ARTIST_GID, artistGid),
                    NavArgument(NavArgs.ARTIST_NAME, artistName)
                )
            }
        }

        composable(NavRoutes.ALBUMS_ROOT) {
            val screenViewModel = hiltViewModel<AlbumsListViewModel>()
            AlbumsListScreen(screenViewModel, bottomNavBar) { albumGid, albumName ->
                navController.navigateTo(
                    NavRoutes.ALBUM,
                    NavArgument(NavArgs.ALBUM_GID, albumGid),
                    NavArgument(NavArgs.ALBUM_NAME, albumName)
                )
            }
        }

        composable(NavRoutes.GENRES_ROOT) {
            val screenViewModel = hiltViewModel<GenresListViewModel>()
            GenresListScreen(screenViewModel, bottomNavBar) { genre ->
                navController.navigateTo(
                    NavRoutes.TRACKS_ROOT,
                    NavArgument(NavArgs.GENRE_NAME, genre)
                )
            }
        }

        composable(
            createNavRoute(NavRoutes.ARTIST, NavArgs.ARTIST_GID, NavArgs.ARTIST_NAME),
        ) {
            val screenViewModel = hiltViewModel<ArtistViewModel>()
            ArtistScreen(screenViewModel, bottomNavBar) { albumGid, albumName ->
                navController.navigateTo(
                    NavRoutes.ALBUM,
                    NavArgument(NavArgs.ALBUM_GID, albumGid),
                    NavArgument(NavArgs.ALBUM_NAME, albumName)
                )
            }
        }

        composable(
            createNavRoute(NavRoutes.ALBUM, NavArgs.ALBUM_GID, NavArgs.ALBUM_NAME),
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

      contextBottomSheet()
    }
}