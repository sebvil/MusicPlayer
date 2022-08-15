package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class PlaylistsListStatePreviewParameterProvider :
    PreviewParameterProvider<PlaylistsListState> {
    override val values = sequenceOf(
        PlaylistsListState(
            playlistsList = listOf(
                ModelListItemState(id = 0, "Pop"),
                ModelListItemState(id = 1, "Rock"),
                ModelListItemState(id = 2, "Tropipop"),
                ModelListItemState(id = 3, "Vallenato")
            ),
            sortOrder = MediaSortOrder.ASCENDING,
            isDialogOpen = false,
        ),
        PlaylistsListState(
            playlistsList = listOf(
                ModelListItemState(id = 0, "Pop"),
                ModelListItemState(id = 1, "Rock"),
                ModelListItemState(id = 2, "Tropipop"),
                ModelListItemState(id = 3, "Vallenato")
            ),
            sortOrder = MediaSortOrder.ASCENDING,
            isDialogOpen = true,
        )
    )
}