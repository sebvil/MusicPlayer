package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.artistListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<ArtistListViewModel>(
        destination = NavigationRoute.ArtistsRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        ArtistListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
        )
    }

}
