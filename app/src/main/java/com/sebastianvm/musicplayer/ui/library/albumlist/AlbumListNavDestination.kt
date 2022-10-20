package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.albumListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<AlbumListViewModel>(
        destination = NavigationRoute.AlbumsRoot,
        destinationType = DestinationType.Screen,
    ) { viewModel ->
        AlbumListScreen(viewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}