package com.sebastianvm.musicplayer.ui.library.root

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination


fun NavGraphBuilder.libraryNavDestination(
    navigationDelegate: NavigationDelegate,
) {
    screenDestination<LibraryViewModel>(
        destination = NavigationRoute.LibraryRoot,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        LibraryRoute(
            viewModel = viewModel,
            navigateToSearchScreen = { navigationDelegate.navigateToScreen(NavigationDestination.Search) },
            navigateToAllTracksList = {
                navigationDelegate.navigateToScreen(
                    NavigationDestination.TrackList(
                        TrackListArguments(trackList = MediaGroup.AllTracks)
                    )
                )
            },
            navigateToArtistList = { navigationDelegate.navigateToScreen(NavigationDestination.ArtistsRoot) },
            navigateToAlbumList = { navigationDelegate.navigateToScreen(NavigationDestination.AlbumsRoot) },
            navigateToGenreList = { navigationDelegate.navigateToScreen(NavigationDestination.GenresRoot) },
            navigateToPlaylistList = { navigationDelegate.navigateToScreen(NavigationDestination.PlaylistsRoot) },
        )
    }
}