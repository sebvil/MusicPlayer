package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
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
    val mediaGroup: MediaGroup,
    val trackIndex: Int = 0,
    val positionInPlaylist: Long? = null
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


private inline fun <reified VM : BaseContextMenuViewModel<*>> NavGraphBuilder.contextBottomSheetDestination(
    destination: NavigationRoute,
    navigationDelegate: NavigationDelegate
) {
    screenDestination<VM>(
        destination = destination,
        destinationType = DestinationType.BottomSheet
    ) { viewModel ->
        ContextBottomSheet(sheetViewModel = viewModel, navigationDelegate = navigationDelegate)
    }
}

fun NavGraphBuilder.contextBottomSheetDestinations(
    navigationDelegate: NavigationDelegate,
) {

    contextBottomSheetDestination<TrackContextMenuViewModel>(
        destination = NavigationRoute.TrackContextMenu,
        navigationDelegate = navigationDelegate
    )

    contextBottomSheetDestination<ArtistContextMenuViewModel>(
        destination = NavigationRoute.ArtistContextMenu,
        navigationDelegate = navigationDelegate
    )

    contextBottomSheetDestination<AlbumContextMenuViewModel>(
        destination = NavigationRoute.AlbumContextMenu,
        navigationDelegate = navigationDelegate
    )

    contextBottomSheetDestination<GenreContextMenuViewModel>(
        destination = NavigationRoute.GenreContextMenu,
        navigationDelegate = navigationDelegate
    )

    contextBottomSheetDestination<PlaylistContextMenuViewModel>(
        destination = NavigationRoute.PlaylistContextMenu,
        navigationDelegate = navigationDelegate
    )
}
