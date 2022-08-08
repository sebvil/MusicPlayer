package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class TrackContextMenuArguments(
    val trackId: Long,
    val mediaType: MediaType,
    val mediaGroup: MediaGroup,
    val trackIndex: Int = 0
) : NavigationArguments

@Serializable
@Parcelize
data class ArtistContextMenuArguments(val artistId: Long) : NavigationArguments

@Serializable
@Parcelize
data class AlbumContextMenuArguments(
    val albumId: Long
) : NavigationArguments

@Serializable
@Parcelize
data class GenreContextMenuArguments(val genreId: Long) : NavigationArguments

@Serializable
@Parcelize
data class PlaylistContextMenuArguments(val playlistId: Long) : NavigationArguments

fun NavGraphBuilder.contextBottomSheet(
    navigationDelegate: NavigationDelegate,
) {
    screenDestination<TrackContextMenuViewModel>(
        NavigationRoute.TrackContextMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }

    screenDestination<ArtistContextMenuViewModel>(
        NavigationRoute.ArtistContextMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }

    screenDestination<AlbumContextMenuViewModel>(
        NavigationRoute.AlbumContextMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }

    screenDestination<GenreContextMenuViewModel>(
        NavigationRoute.GenreContextMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }

    screenDestination<PlaylistContextMenuViewModel>(
        NavigationRoute.PlaylistContextMenu,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}
