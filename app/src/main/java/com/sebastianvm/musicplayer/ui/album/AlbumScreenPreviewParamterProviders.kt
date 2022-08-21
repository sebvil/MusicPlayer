package com.sebastianvm.musicplayer.ui.album

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumId = 0,
            imageUri = "",
            albumName = "10:20:40",
        )
    )
}
