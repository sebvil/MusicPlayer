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
import com.sebastianvm.musicplayer.ui.library.playlistlist.playlistListNavDestination
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
    val navigationDelegate = NavigationDelegate(navController)
    Scaffold(bottomBar = { BottomNavBar(navigationDelegate) }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationRoute.Library.name,
            modifier = Modifier.padding(paddingValues)
        ) {

            libraryGraph(navigationDelegate)

            queueNavDestination(navigationDelegate)
            musicPlayerNavDestination(navigationDelegate)
        }
    }
}

fun NavGraphBuilder.libraryGraph(navigationDelegate: NavigationDelegate) {

    navigation(
        startDestination = NavigationRoute.LibraryRoot.name,
        route = NavigationRoute.Library.name
    ) {
        libraryNavDestination(navigationDelegate)

        searchNavDestination(navigationDelegate)

        trackListNavDestination(navigationDelegate)
        artistListNavDestination(navigationDelegate)
        albumListNavDestination(navigationDelegate)
        genreListNavDestination(navigationDelegate)
        playlistListNavDestination(navigationDelegate)

        artistNavDestination(navigationDelegate)
        albumNavDestination(navigationDelegate)
        playlistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)

        sortBottomSheetNavDestination(navigationDelegate)
        contextBottomSheet(navigationDelegate)
        artistsBottomSheetNavDestination(navigationDelegate)
    }
}