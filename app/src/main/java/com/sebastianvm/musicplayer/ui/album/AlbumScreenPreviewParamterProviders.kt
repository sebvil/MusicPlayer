package com.sebastianvm.musicplayer.ui.album

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumId = 0,
            imageUri = "",
            albumName = "10:20:40",
            trackList = listOf(
                ModelListItemState.Basic(
                    id = 0,
                    headlineText = "La Promesa",
                    supportingText = "Melendi"
                ),
                ModelListItemState.Basic(
                    id = 1,
                    headlineText = "La Promesa",
                    supportingText = "Melendi"
                ),
                ModelListItemState.Basic(
                    id = 2,
                    headlineText = "La Promesa",
                    supportingText = "Melendi"
                ),
                ModelListItemState.Basic(
                    id = 3,
                    headlineText = "La Promesa",
                    supportingText = "Melendi"
                )
            ),
        )
    )
}
