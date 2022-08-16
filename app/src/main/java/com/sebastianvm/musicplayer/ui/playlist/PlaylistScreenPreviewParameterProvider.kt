package com.sebastianvm.musicplayer.ui.playlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemStateWithPosition

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
                ModelListItemStateWithPosition(
                    position = 0,
                    ModelListItemState(
                        id = 0,
                        headlineText = "La Promesa",
                        supportingText = "Melendi"
                    )
                ),
                ModelListItemStateWithPosition(
                    position = 1,
                    ModelListItemState(
                        id = 0,
                        headlineText = "La Promesa",
                        supportingText = "Melendi"
                    )
                ),
                ModelListItemStateWithPosition(
                    position = 2,
                    ModelListItemState(
                        id = 0,
                        headlineText = "La Promesa",
                        supportingText = "Melendi"
                    )
                ),
                ModelListItemStateWithPosition(
                    position = 3,
                    ModelListItemState(
                        id = 0,
                        headlineText = "La Promesa",
                        supportingText = "Melendi"
                    )
                )
            ),
            playbackResult = null
        )
    )
}