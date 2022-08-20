package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class PlaylistListStatePreviewParameterProvider :
    PreviewParameterProvider<PlaylistListState> {
    override val values = sequenceOf(
        PlaylistListState(
            playlistList = listOf(
                ModelListItemState(id = 0, "Pop"),
                ModelListItemState(id = 1, "Rock"),
                ModelListItemState(id = 2, "Tropipop"),
                ModelListItemState(id = 3, "Vallenato")
            ),
            isCreatePlaylistDialogOpen = false,
            isPlaylistCreationErrorDialogOpen = false
        ),
        PlaylistListState(
            playlistList = listOf(
                ModelListItemState(id = 0, "Pop"),
                ModelListItemState(id = 1, "Rock"),
                ModelListItemState(id = 2, "Tropipop"),
                ModelListItemState(id = 3, "Vallenato")
            ),
            isCreatePlaylistDialogOpen = true,
            isPlaylistCreationErrorDialogOpen = false
        )
    )
}