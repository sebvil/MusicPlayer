package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.genreListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<GenreListViewModel>(
        destination = NavigationRoute.GenresRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        GenreListScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate,
        )
    }
}
