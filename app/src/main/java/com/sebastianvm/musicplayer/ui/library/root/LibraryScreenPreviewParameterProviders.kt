package com.sebastianvm.musicplayer.ui.library.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.R


class LibraryStateProvider : PreviewParameterProvider<LibraryState> {
    override val values = sequenceOf(
        LibraryState(
            libraryItems = listOf(
                LibraryItem(
                    "0",
                    R.string.all_songs,
                    R.plurals.number_of_tracks,
                    R.drawable.ic_song,
                    100
                ),
                LibraryItem(
                    "1",
                    R.string.artists,
                    R.plurals.number_of_artists,
                    R.drawable.ic_artist,
                    5
                ),
                LibraryItem(
                    "2",
                    R.string.albums,
                    R.plurals.number_of_albums,
                    R.drawable.ic_album,
                    10
                ),
                LibraryItem(
                    "3",
                    R.string.genres,
                    R.plurals.number_of_genres,
                    R.drawable.ic_genre,
                    3
                )
            ),
            showPermissionDeniedDialog = false,
            showPermissionExplanationDialog = false,
        )
    )
}


class LibraryItemListProvider : PreviewParameterProvider<List<LibraryItem>> {
    override val values = sequenceOf(
        listOf(
            LibraryItem(
                "0",
                R.string.all_songs,
                R.plurals.number_of_tracks,
                R.drawable.ic_song,
                100
            ),
            LibraryItem(
                "1",
                R.string.artists,
                R.plurals.number_of_artists,
                R.drawable.ic_artist,
                5
            ),
            LibraryItem(
                "2",
                R.string.albums,
                R.plurals.number_of_albums,
                R.drawable.ic_album,
                10
            ),
            LibraryItem(
                "3",
                R.string.genres,
                R.plurals.number_of_genres,
                R.drawable.ic_genre,
                3
            )
        )
    )
}


class LibraryItemProvider : PreviewParameterProvider<LibraryItem> {
    override val values: Sequence<LibraryItem>
        get() = sequenceOf(
            LibraryItem(
                "0",
                R.string.all_songs,
                R.plurals.number_of_tracks,
                R.drawable.ic_song,
                10
            )
        )
}