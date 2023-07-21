package com.sebastianvm.musicplayer.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.sebastianvm.musicplayer.ui.artist.artistNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.context.contextBottomSheetDestinations
import com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists.artistsBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.sortBottomSheetNavDestination
import com.sebastianvm.musicplayer.ui.playlist.trackSearchNavDestination
import com.sebastianvm.musicplayer.ui.queue.queueNavDestination


fun NavGraphBuilder.libraryGraph(
    navigationDelegate: NavigationDelegate,
) {

    navigation(
        startDestination = NavigationRoute.MainRoot.name,
        route = NavigationRoute.Main.name
    ) {


        artistNavDestination(navigationDelegate)

        trackSearchNavDestination(navigationDelegate)


        sortBottomSheetNavDestination(navigationDelegate)
        contextBottomSheetDestinations(navigationDelegate)
        artistsBottomSheetNavDestination(navigationDelegate)
        queueNavDestination(navigationDelegate)

    }
}