package com.sebastianvm.musicplayer.ui.library.genrelist

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination

fun NavGraphBuilder.genreListNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<GenreListViewModel>(
        destination = NavigationRoute.GenresRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        GenreListRoute(
            viewModel = viewModel,
            navigateToGenre = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackList(
                        args
                    )
                )
            },
            openGenreContextMenu = { args ->
                navigationDelegate.navigateToScreen(
                    NavigationDestination.GenreContextMenu(args)
                )
            },
            navigateBack = { navigationDelegate.navigateUp() }
        )
    }
}
