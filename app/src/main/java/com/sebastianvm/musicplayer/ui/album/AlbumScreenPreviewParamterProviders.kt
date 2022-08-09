package com.sebastianvm.musicplayer.ui.album

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.TrackRowState

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumId = 0,
            imageUri = Uri.EMPTY,
            albumName = "10:20:40",
            listOf(
                TrackRowState(0, 0, "La Promesa", "Melendi"),
                TrackRowState(1, 1, "La Promesa", "Melendi"),
                TrackRowState(2, 2, "La Promesa", "Melendi"),
                TrackRowState(3, 3, "La Promesa", "Melendi")
            ),
        )
    )
}
