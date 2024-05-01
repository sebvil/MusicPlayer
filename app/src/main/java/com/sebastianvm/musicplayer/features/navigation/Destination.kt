package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.NoArguments

sealed interface Destination {
    val arguments: Arguments

    data object Root : Destination {
        override val arguments: Arguments = NoArguments
    }

    data class TrackList(override val arguments: TrackListArguments) : Destination
    data class Artist(override val arguments: ArtistArguments) : Destination
    data class TrackSearch(override val arguments: TrackSearchArguments) : Destination
}