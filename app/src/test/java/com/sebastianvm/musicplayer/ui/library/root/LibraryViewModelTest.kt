package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest : BaseTest() {

    private fun generateViewModel(): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = 0),
                    LibraryItem.Artists(count = 0),
                    LibraryItem.Albums(count = 0),
                    LibraryItem.Genres(count = 0),
                    LibraryItem.Playlists(count = 0)
                ),
            ),
            musicRepository = FakeMusicRepository(),
        )
    }

    @Test
    fun `init updates counts`() = testScope.runReliableTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            assertEquals(
                listOf(
                    LibraryItem.Tracks(count = FakeMusicRepository.FAKE_TRACK_COUNTS),
                    LibraryItem.Artists(count = FakeMusicRepository.FAKE_ARTIST_COUNTS),
                    LibraryItem.Albums(count = FakeMusicRepository.FAKE_ALBUM_COUNTS),
                    LibraryItem.Genres(count = FakeMusicRepository.FAKE_GENRE_COUNTS),
                    LibraryItem.Playlists(count = FakeMusicRepository.FAKE_PLAYLIST_COUNTS)
                ),
                state.value.libraryItems
            )
        }
    }


    @Test
    fun `RowClicked adds NavigateToScreen event`() {
        with(generateViewModel()) {
            handle(LibraryUserAction.RowClicked(NavigationDestination.GenresRoot))
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(NavigationDestination.GenresRoot)
            )
        }
    }

    @Test
    fun `SearchBoxClicked navigates to search screen`() {
        with(generateViewModel()) {
            handle(LibraryUserAction.SearchBoxClicked)
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(NavigationDestination.Search)
            )
        }
    }


}
