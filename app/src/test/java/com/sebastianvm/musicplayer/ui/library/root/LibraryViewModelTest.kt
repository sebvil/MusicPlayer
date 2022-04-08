package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

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
            ioDispatcher = Dispatchers.Main
        )
    }

    @Test
    fun `init updates counts`() = runTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            assertEquals(
                listOf(
                    LibraryItem.Tracks(count = FakeMusicRepository.FAKE_TRACK_COUNTS),
                    LibraryItem.Artists(count = FakeMusicRepository.FAKE_ARTIST_COUNTS),
                    LibraryItem.Albums(count = FakeMusicRepository.FAKE_ALBUM_COUNTS),
                    LibraryItem.Genres(count = FakeMusicRepository.FAKE_GENRE_COUNTS),
                    LibraryItem.Playlists(count = FakeMusicRepository.FAKE_PLAYLIST_COUNTS)
                ), state.value.libraryItems
            )
        }
    }


    @Test
    fun `RowClicked adds nav NavigateToScreen event`() {
        with(generateViewModel()) {
            onRowClicked(NavRoutes.TRACKS_ROOT)
            assertContains(
                events.value,
                LibraryUiEvent.NavigateToScreen(rowId = NavRoutes.TRACKS_ROOT)
            )
        }
    }

}
