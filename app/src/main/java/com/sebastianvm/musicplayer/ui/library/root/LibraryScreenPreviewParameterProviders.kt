package com.sebastianvm.musicplayer.ui.library.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider


class LibraryStateProvider : PreviewParameterProvider<LibraryState> {
    override val values = sequenceOf(
        LibraryState(
            libraryItems = listOf(
                LibraryItem.Tracks(count = 1000),
                LibraryItem.Artists(count = 10),
                LibraryItem.Albums(count = 100),
                LibraryItem.Genres(count = 1)
            ),
            events = listOf()
        )
    )
}


class LibraryItemListProvider : PreviewParameterProvider<List<LibraryItem>> {
    override val values = sequenceOf(
        listOf(
            LibraryItem.Tracks(count = 1000),
            LibraryItem.Artists(count = 10),
            LibraryItem.Albums(count = 100),
            LibraryItem.Genres(count = 1)
        )
    )
}
