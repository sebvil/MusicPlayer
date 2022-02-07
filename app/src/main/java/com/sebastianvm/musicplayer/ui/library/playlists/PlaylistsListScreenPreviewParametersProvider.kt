package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder

class PlaylistsListStatePreviewParameterProvider :
    PreviewParameterProvider<PlaylistsListState> {
    override val values = sequenceOf(
        PlaylistsListState(
            playlistsList = listOf(
                Playlist("Pop"),
                Playlist("Rock"),
                Playlist("Tropipop"),
                Playlist("Vallenato")
            ),
            sortOrder = MediaSortOrder.ASCENDING,
            isDialogOpen = false
        ),
        PlaylistsListState(
            playlistsList = listOf(
                Playlist("Pop"),
                Playlist("Rock"),
                Playlist("Tropipop"),
                Playlist("Vallenato")
            ),
            sortOrder = MediaSortOrder.ASCENDING,
            isDialogOpen = true
        )
    )
}