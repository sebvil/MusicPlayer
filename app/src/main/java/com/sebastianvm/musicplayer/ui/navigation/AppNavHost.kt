package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.sebastianvm.musicplayer.ui.album.albumNavDestination
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.artistsBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.sortBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.library.albums.albumsListNavDestination
import com.sebastianvm.musicplayer.ui.library.artists.artistsNavDestination
import com.sebastianvm.musicplayer.ui.library.genres.genresListNavDestination
import com.sebastianvm.musicplayer.ui.library.playlists.playlistsListNavDestination
import com.sebastianvm.musicplayer.ui.library.root.libraryNavDestination
import com.sebastianvm.musicplayer.ui.library.tracks.tracksListNavDestination
import com.sebastianvm.musicplayer.ui.player.musicPlayerNavDestination
import com.sebastianvm.musicplayer.ui.queue.queueNavDestination
import com.sebastianvm.musicplayer.ui.search.searchNavDestination

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

            queueNavDestination()
            musicPlayerNavDestination()

            searchNavDestination(navController)
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.libraryGraph(
    navController: NavHostController,
    requestPermission: (String) -> String,
) {

    navigation(startDestination = NavRoutes.LIBRARY_ROOT, route = NavRoutes.LIBRARY) {
        libraryNavDestination(navController, requestPermission)

        tracksListNavDestination(navController)
        artistsNavDestination(navController)
        albumsListNavDestination(navController)
        genresListNavDestination(navController)
        playlistsListNavDestination(navController)

        artistNavDestination(navController)
        albumNavDestination(navController)

        sortBottomSheetNavDestination(navController)
        contextBottomSheet(navController)
        artistsBottomSheetNavDestination(navController)
    }
}