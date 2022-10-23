package com.sebastianvm.musicplayer.ui.library.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.util.compose.ComponentPreview
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import com.sebastianvm.musicplayer.ui.util.compose.ThemedPreview
import com.sebastianvm.musicplayer.ui.util.mvvm.DefaultScreenDelegateProvider
import kotlin.random.Random


class LibraryStatePreviewParamProvider : PreviewParameterProvider<LibraryState> {
    override val values: Sequence<LibraryState>
        get() = sequenceOf(
            LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = 0),
                    LibraryItem.Artists(count = 0),
                    LibraryItem.Albums(count = 0),
                    LibraryItem.Genres(count = 0),
                    LibraryItem.Playlists(count = 0),
                )
            ),
            LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = 1),
                    LibraryItem.Artists(count = 1),
                    LibraryItem.Albums(count = 1),
                    LibraryItem.Genres(count = 1),
                    LibraryItem.Playlists(count = 1),
                )
            ),
            LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = Random.nextInt(0, 1000)),
                    LibraryItem.Artists(count = Random.nextInt(0, 1000)),
                    LibraryItem.Albums(count = Random.nextInt(0, 1000)),
                    LibraryItem.Genres(count = Random.nextInt(0, 1000)),
                    LibraryItem.Playlists(count = Random.nextInt(0, 1000)),
                )
            )
        )
}

@ComponentPreview
@Composable
fun SearchBoxPreview() {
    ThemedPreview {
        SearchBox()
    }
}

@ScreenPreview
@Composable
private fun LibraryScreenPreview(@PreviewParameter(LibraryStatePreviewParamProvider::class) state: LibraryState) {
    ScreenPreview {
        LibraryScreen(
            state = state,
            screenDelegate = DefaultScreenDelegateProvider.getDefaultInstance(),
        )
    }
}