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


        searchNavDestination(navigationDelegate)

        trackListNavDestination(navigationDelegate)

        artistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)

//        sortBottomSheetNavDestination(navigationDelegate)
//        contextBottomSheetDestinations(navigationDelegate)
//        artistsBottomSheetNavDestination(navigationDelegate)
        queueNavDestination(navigationDelegate)

    }
}