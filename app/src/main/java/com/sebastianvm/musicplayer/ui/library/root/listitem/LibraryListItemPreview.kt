package com.sebastianvm.musicplayer.ui.library.root.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import kotlin.random.Random


class LibraryListItemPreviewParamProvider : PreviewParameterProvider<LibraryItem> {
    override val values: Sequence<LibraryItem>
        get() = sequenceOf(0, 1, Random.nextInt(0, 1000)).flatMap {
            sequenceOf(
                LibraryItem.Tracks(count = it),
                LibraryItem.Artists(count = it),
                LibraryItem.Albums(count = it),
                LibraryItem.Genres(count = it),
                LibraryItem.Playlists(count = it),
            )
        }

}

@ComponentPreview
@Composable
fun LibraryListItemPreview(@PreviewParameter(LibraryListItemPreviewParamProvider::class) item: LibraryItem) {
    ThemedPreview {
        LibraryListItem(item = item, onItemClicked = {})
    }
}