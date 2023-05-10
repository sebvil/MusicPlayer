package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.sebastianvm.musicplayer.ui.Screens
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.library.albumlist.albumListNavDestination
import com.sebastianvm.musicplayer.ui.library.artistlist.artistListNavDestination
import com.sebastianvm.musicplayer.ui.library.genrelist.genreListNavDestination
import com.sebastianvm.musicplayer.ui.library.playlistlist.playlistListNavDestination
import com.sebastianvm.musicplayer.ui.library.root.libraryNavDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.trackListNavDestination
import com.sebastianvm.musicplayer.ui.mainNavDestination
import com.sebastianvm.musicplayer.ui.playlist.trackSearchNavDestination
import com.sebastianvm.musicplayer.ui.queue.queueNavDestination
import com.sebastianvm.musicplayer.ui.search.searchNavDestination

@Composable
fun AppNavHost(navController: NavHostController) {
    val navigationDelegate = NavigationDelegateImpl(navController)

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Main.name,
    ) {

        libraryGraph(navigationDelegate)
    }

}


fun NavGraphBuilder.libraryGraph(
    navigationDelegate: NavigationDelegate,
) {

    navigation(
        startDestination = NavigationRoute.MainRoot.name,
        route = NavigationRoute.Main.name
    ) {

        mainNavDestination(navigationDelegate, PaddingValues(0.dp)) { page, paddingValues ->
            Screens(page = page, navigationDelegate = navigationDelegate)
        }

        libraryNavDestination(navigationDelegate)

        searchNavDestination(navigationDelegate)

        trackListNavDestination(navigationDelegate)
        artistListNavDestination(navigationDelegate)
        albumListNavDestination(navigationDelegate)
        genreListNavDestination(navigationDelegate)
        playlistListNavDestination(navigationDelegate)

        artistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)

//        sortBottomSheetNavDestination(navigationDelegate)
//        contextBottomSheetDestinations(navigationDelegate)
//        artistsBottomSheetNavDestination(navigationDelegate)
        queueNavDestination(navigationDelegate)

    }
}