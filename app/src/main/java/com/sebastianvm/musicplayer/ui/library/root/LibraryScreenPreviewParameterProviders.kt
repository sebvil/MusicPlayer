package com.sebastianvm.musicplayer.ui.library.root

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jayasuryat.dowel.annotation.ConsiderForDowel


@ConsiderForDowel
class LibraryStateProvider : PreviewParameterProvider<List<LibraryItem>> {
    override val values = sequenceOf(
        listOf(
            LibraryItem.Tracks(count = 1000),
            LibraryItem.Artists(count = 10),
            LibraryItem.Albums(count = 100),
            LibraryItem.Genres(count = 1),
            LibraryItem.Playlists(count = 0)
        ),
    )

}
