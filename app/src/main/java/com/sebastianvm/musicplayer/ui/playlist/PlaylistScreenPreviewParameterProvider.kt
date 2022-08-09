package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class PlaylistStatePreviewParameterProvider : PreviewParameterProvider<PlaylistState> {
    override val values = sequenceOf(
        PlaylistState(
            playlistId = 0,
            playlistName = "My playlist",
            listOf(),
            playbackResult = null
        ),
        PlaylistState(
            playlistId = 0,
            playlistName = "My playlist",
            listOf(
                TrackRowState(
                    id = 0,
                    trackId = 0,
                    trackName = "Track",
                    artists = "Artist",
                )
            ),
            playbackResult = null
        )
    )
}