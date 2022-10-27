package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.library.root.listitem.LibraryItem
import com.sebastianvm.musicplayer.util.BaseTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest : BaseTest() {

    private fun generateViewModel(): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                tracksItem = LibraryItem.Tracks(count = 0),
                artistsItem = LibraryItem.Artists(count = 0),
                albumsItem = LibraryItem.Albums(count = 0),
                genresItem = LibraryItem.Genres(count = 0),
                playlistsItem = LibraryItem.Playlists(count = 0)
            ),
            musicRepository = FakeMusicRepository(),
        )
    }

    @Test
    fun `init updates counts`() = testScope.runReliableTest {
        with(generateViewModel()) {
            assertEquals(
                LibraryState(
                    tracksItem = LibraryItem.Tracks(count = FakeMusicRepository.FAKE_TRACK_COUNTS),
                    artistsItem = LibraryItem.Artists(count = FakeMusicRepository.FAKE_ARTIST_COUNTS),
                    albumsItem = LibraryItem.Albums(count = FakeMusicRepository.FAKE_ALBUM_COUNTS),
                    genresItem = LibraryItem.Genres(count = FakeMusicRepository.FAKE_GENRE_COUNTS),
                    playlistsItem = LibraryItem.Playlists(count = FakeMusicRepository.FAKE_PLAYLIST_COUNTS)
                ),
                state.libraryItems
            )
        }
    }

}
