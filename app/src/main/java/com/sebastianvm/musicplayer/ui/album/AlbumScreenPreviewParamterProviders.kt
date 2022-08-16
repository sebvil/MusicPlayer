package com.sebastianvm.musicplayer.ui.album

import android.net.Uri
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class AlbumStatePreviewParameterProvider :
    PreviewParameterProvider<AlbumState> {
    override val values = sequenceOf(
        AlbumState(
            albumId = 0,
            imageUri = Uri.EMPTY,
            albumName = "10:20:40",
            listOf(
                ModelListItemState(id = 0, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 1, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 2, headlineText = "La Promesa", supportingText = "Melendi"),
                ModelListItemState(id = 3, headlineText = "La Promesa", supportingText = "Melendi")
            ),
        )
    )
}
