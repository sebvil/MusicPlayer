package com.sebastianvm.musicplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.trackListNavDestination
import com.sebastianvm.musicplayer.ui.mainNavDestination
import com.sebastianvm.musicplayer.ui.playlist.trackSearchNavDestination
import com.sebastianvm.musicplayer.ui.queue.queueNavDestination

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

        mainNavDestination(navigationDelegate)
        
        trackListNavDestination(navigationDelegate)

        artistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)

//        sortBottomSheetNavDestination(navigationDelegate)
//        contextBottomSheetDestinations(navigationDelegate)
//        artistsBottomSheetNavDestination(navigationDelegate)
        queueNavDestination(navigationDelegate)

    }
}