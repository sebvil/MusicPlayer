package com.sebastianvm.musicplayer.ui.library.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import com.sebastianvm.musicplayer.ui.util.compose.ScreenPreview
import kotlin.random.Random


class LibraryStatePreviewParamProvider : PreviewParameterProvider<LibraryState> {
    override val values: Sequence<LibraryState>
        get() = sequenceOf(
            LibraryState(
                tracksItem = LibraryItem.Tracks(count = 0),
                artistsItem = LibraryItem.Artists(count = 0),
                albumsItem = LibraryItem.Albums(count = 0),
                genresItem = LibraryItem.Genres(count = 0),
                playlistsItem = LibraryItem.Playlists(count = 0),
            ),
            LibraryState(
                tracksItem = LibraryItem.Tracks(count = 1),
                artistsItem = LibraryItem.Artists(count = 1),
                albumsItem = LibraryItem.Albums(count = 1),
                genresItem = LibraryItem.Genres(count = 1),
                playlistsItem = LibraryItem.Playlists(count = 1),
            ),
            LibraryState(
                tracksItem = LibraryItem.Tracks(count = Random.nextInt(0, 1000)),
                artistsItem = LibraryItem.Artists(count = Random.nextInt(0, 1000)),
                albumsItem = LibraryItem.Albums(count = Random.nextInt(0, 1000)),
                genresItem = LibraryItem.Genres(count = Random.nextInt(0, 1000)),
                playlistsItem = LibraryItem.Playlists(count = Random.nextInt(0, 1000)),
            )
        )
}


@ScreenPreview
@Composable
private fun LibraryScreenPreview(@PreviewParameter(LibraryStatePreviewParamProvider::class) state: LibraryState) {
    ScreenPreview {
        LibraryScreen(
            state = state,
            navigateToSearchScreen = {},
            navigateToAllTracksList = {},
            navigateToArtistList = {},
            navigateToAlbumList = {},
            navigateToGenreList = {},
            navigateToPlaylistList = {},
        )
    }
}