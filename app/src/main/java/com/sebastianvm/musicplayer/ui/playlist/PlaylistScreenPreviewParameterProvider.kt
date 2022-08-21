package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class PlaylistStatePreviewParameterProvider : PreviewParameterProvider<PlaylistState> {
    override val values = sequenceOf(
        PlaylistState(
            playlistId = 0,
            playlistName = "My playlist",
        ),
        PlaylistState(
            playlistId = 0,
            playlistName = "My playlist",
        )
    )
}