package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.repository.music.CountHolder
import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import com.sebastianvm.musicplayer.util.BaseTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest : BaseTest() {

    private val countHolderFlow = MutableStateFlow(
        CountHolder(
            tracks = 0,
            artists = 0,
            albums = 0,
            genres = 0,
            playlists = 0
        )
    )

    private fun generateViewModel(): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                tracksItem = LibraryItem.Tracks(count = 0),
                artistsItem = LibraryItem.Artists(count = 0),
                albumsItem = LibraryItem.Albums(count = 0),
                genresItem = LibraryItem.Genres(count = 0),
                playlistsItem = LibraryItem.Playlists(count = 0)
            ),
            musicRepository = FakeMusicRepository(countHolderFlow)
        )
    }

    @Test
    fun `init subscribes to count updates`() = testScope.runReliableTest {
        with(generateViewModel()) {
            assertEquals(
                LibraryState(
                    tracksItem = LibraryItem.Tracks(count = 0),
                    artistsItem = LibraryItem.Artists(count = 0),
                    albumsItem = LibraryItem.Albums(count = 0),
                    genresItem = LibraryItem.Genres(count = 0),
                    playlistsItem = LibraryItem.Playlists(count = 0)
                ),
                state
            )
            countHolderFlow.value = CountHolder(
                tracks = 5,
                artists = 4,
                albums = 3,
                genres = 2,
                playlists = 1
            )

            assertEquals(
                LibraryState(
                    tracksItem = LibraryItem.Tracks(count = 5),
                    artistsItem = LibraryItem.Artists(count = 4),
                    albumsItem = LibraryItem.Albums(count = 3),
                    genresItem = LibraryItem.Genres(count = 2),
                    playlistsItem = LibraryItem.Playlists(count = 1)
                ),
                state
            )
        }
    }
}
