package com.sebastianvm.musicplayer.ui.library.genre

import androidx.navigation.NavGraphBuilder
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.HasTrackList
import com.sebastianvm.musicplayer.ui.components.lists.tracklist.TrackListComponentArgs
import com.sebastianvm.musicplayer.ui.navigation.DestinationType
import com.sebastianvm.musicplayer.ui.navigation.NavigationArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDelegate
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.navigation.screenDestination
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@kotlinx.serialization.Serializable
@Parcelize
data class GenreArguments(val genreId: Long) :
    NavigationArguments, HasTrackList {
    @IgnoredOnParcel
    override val args: TrackListComponentArgs = TrackListComponentArgs(
        trackListId = genreId,
        trackListType = TrackListType.GENRE
    )
}

fun NavGraphBuilder.genreNavDestination(navigationDelegate: NavigationDelegate) {
    screenDestination<GenreViewModel>(
        destination = NavigationRoute.Genre,
        destinationType = DestinationType.Screen
    ) { viewModel ->
        GenreScreen(
            screenViewModel = viewModel,
            navigationDelegate = navigationDelegate
        )
    }
}
