package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sebastianvm.musicplayer.ui.album.AlbumScreen
import com.sebastianvm.musicplayer.ui.album.AlbumViewModel
import com.sebastianvm.musicplayer.ui.artist.ArtistScreen
import com.sebastianvm.musicplayer.ui.artist.ArtistViewModel
import com.sebastianvm.musicplayer.ui.library.LibraryScreen
import com.sebastianvm.musicplayer.ui.library.LibraryScreenActivityDelegate
import com.sebastianvm.musicplayer.ui.library.LibraryViewModel
import com.sebastianvm.musicplayer.ui.library.albums.AlbumsListScreen
import com.sebastianvm.musicplayer.ui.library.albums.AlbumsListViewModel
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsListScreen
import com.sebastianvm.musicplayer.ui.library.artists.ArtistsListViewModel
import com.sebastianvm.musicplayer.ui.library.genres.GenresListScreen
import com.sebastianvm.musicplayer.ui.library.genres.GenresListViewModel
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListScreen
import com.sebastianvm.musicplayer.ui.library.tracks.TracksListViewModel
import com.sebastianvm.musicplayer.ui.player.MusicPlayerScreen
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    requestPermission: (String) -> String,
    openAppSettings: () -> Unit
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = NavRoutes.LIBRARY
    ) {

        libraryGraph(
            navController = navController,
            requestPermission = requestPermission,
            openAppSettings = openAppSettings
        )

        composable(NavRoutes.PLAYER) {
            val screenViewModel = hiltViewModel<MusicPlayerViewModel>()
            MusicPlayerScreen(screenViewModel)
        }


    }

}

fun NavGraphBuilder.libraryGraph(
    navController: NavHostController,
    requestPermission: (String) -> String,
    openAppSettings: () -> Unit
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

                    override fun openAppSettings() {
                        openAppSettings()
                    }

                    override fun navigateToLibraryScreen(route: String) {
                        navController.navigate(route = route)
                    }

                })
        }
        composable(
            createNavRoute(NavRoutes.TRACKS_ROOT, NavArgs.GENRE_NAME),
            arguments = listOf(navArgument(NavArgs.GENRE_NAME) {
                nullable = true
                type = NavType.StringType
            })
        ) {
            val screenViewModel = hiltViewModel<TracksListViewModel>()
            TracksListScreen(screenViewModel) {
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

        composable(NavRoutes.ARTISTS_ROOT) {
            val screenViewModel = hiltViewModel<ArtistsListViewModel>()
            ArtistsListScreen(screenViewModel) { artistGid, artistName ->
                navController.navigateTo(
                    NavRoutes.ARTIST,
                    NavArgument(NavArgs.ARTIST_GID, artistGid),
                    NavArgument(NavArgs.ARTIST_NAME, artistName)
                )
            }
        }

        composable(NavRoutes.ALBUMS_ROOT) {
            val screenViewModel = hiltViewModel<AlbumsListViewModel>()
            AlbumsListScreen(screenViewModel) { albumGid, albumName ->
                navController.navigateTo(
                    NavRoutes.ALBUM,
                    NavArgument(NavArgs.ALBUM_GID, albumGid),
                    NavArgument(NavArgs.ALBUM_NAME, albumName)
                )
            }
        }

        composable(NavRoutes.GENRES_ROOT) {
            val screenViewModel = hiltViewModel<GenresListViewModel>()
            GenresListScreen(screenViewModel) { genre ->
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
            ArtistScreen(screenViewModel) { albumGid, albumName ->
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
            AlbumScreen(screenViewModel) {
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
}