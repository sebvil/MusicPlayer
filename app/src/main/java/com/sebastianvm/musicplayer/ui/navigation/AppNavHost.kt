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
import com.sebastianvm.musicplayer.ui.album.albumNavDestination
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheet
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.artistsBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.sortBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.library.albumlist.albumListNavDestination
import com.sebastianvm.musicplayer.ui.library.artistlist.artistListNavDestination
import com.sebastianvm.musicplayer.ui.library.genrelist.genreListNavDestination
import com.sebastianvm.musicplayer.ui.library.playlists.playlistsListNavDestination
import com.sebastianvm.musicplayer.ui.library.root.libraryNavDestination
import com.sebastianvm.musicplayer.ui.library.tracks.trackListNavDestination
import com.sebastianvm.musicplayer.ui.player.musicPlayerNavDestination
import com.sebastianvm.musicplayer.ui.playlist.playlistNavDestination
import com.sebastianvm.musicplayer.ui.playlist.trackSearchNavDestination
import com.sebastianvm.musicplayer.ui.queue.queueNavDestination
import com.sebastianvm.musicplayer.ui.search.searchNavDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    val bottomNavBar = @Composable { BottomNavBar(navController = navController) }
    val navigationDelegate = NavigationDelegate(navController)
    Scaffold(bottomBar = bottomNavBar) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.Library.name,
            modifier = Modifier.padding(paddingValues)
        ) {

            libraryGraph(navigationDelegate = navigationDelegate, navController = navController)

            queueNavDestination()
            musicPlayerNavDestination()

            searchNavDestination(navigationDelegate, navController)
        }
    }
}

fun NavGraphBuilder.libraryGraph(
    navigationDelegate: NavigationDelegate,
    navController: NavHostController
) {

    navigation(
        startDestination = NavigationRoute.LibraryRoot.name,
        route = NavigationRoute.Library.name
    ) {
        libraryNavDestination(navigationDelegate)

        trackListNavDestination(navigationDelegate, navController)
        artistListNavDestination(navigationDelegate, navController)
        albumListNavDestination(navigationDelegate, navController)
        genreListNavDestination(navigationDelegate, navController)
        playlistsListNavDestination(navigationDelegate, navController)

        artistNavDestination(navigationDelegate, navController)
        albumNavDestination(navigationDelegate, navController)
        playlistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)

        sortBottomSheetNavDestination(navController)
        contextBottomSheet(navigationDelegate, navController)
        artistsBottomSheetNavDestination(navController)
    }
}