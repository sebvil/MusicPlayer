package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState

class PlaylistListStatePreviewParameterProvider :
    PreviewParameterProvider<PlaylistListState> {
    override val values = sequenceOf(
        PlaylistListState(
            playlistList = listOf(
                ModelListItemState.Basic(id = 0, "Pop"),
                ModelListItemState.Basic(id = 1, "Rock"),
                ModelListItemState.Basic(id = 2, "Tropipop"),
                ModelListItemState.Basic(id = 3, "Vallenato")
            ),
            isCreatePlaylistDialogOpen = false,
            isPlaylistCreationErrorDialogOpen = false
        ),
        PlaylistListState(
            playlistList = listOf(
                ModelListItemState.Basic(id = 0, "Pop"),
                ModelListItemState.Basic(id = 1, "Rock"),
                ModelListItemState.Basic(id = 2, "Tropipop"),
                ModelListItemState.Basic(id = 3, "Vallenato")
            ),
            isCreatePlaylistDialogOpen = true,
            isPlaylistCreationErrorDialogOpen = false
        )
    )
}