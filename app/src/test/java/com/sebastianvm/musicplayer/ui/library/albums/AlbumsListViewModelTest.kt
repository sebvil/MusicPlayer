package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.uri.FakeUriUtilsRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AlbumsListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    @get:Rule
    val fakeUriUtilsRule = FakeUriUtilsRule()

    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(
            initialState = AlbumsListState(
                albumsList = listOf(),
                currentSort = MediaSortOption.YEAR,
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            albumRepository = FakeAlbumRepository(),
            preferencesRepository = FakePreferencesRepository()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state values`() = runTest {

        with(generateViewModel()) {
            delay(1)
            assertEquals(2, state.value.albumsList.size)
            val albumRow1 = state.value.albumsList[0]
            assertEquals(ALBUM_ID_0, albumRow1.albumId)
            assertEquals(ALBUM_NAME_0, albumRow1.albumName)
            assertEquals(
                "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${ALBUM_ID_0}",
                albumRow1.imageUri
            )
            assertEquals(ALBUM_YEAR_0, albumRow1.year)
            assertEquals(ARTIST_NAME_0, albumRow1.artists)

            val albumRow2 = state.value.albumsList[1]
            assertEquals(ALBUM_ID_1, albumRow2.albumId)
            assertEquals(ALBUM_NAME_1, albumRow2.albumName)
            assertEquals(
                "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${ALBUM_ID_1}",
                albumRow2.imageUri
            )
            assertEquals(ALBUM_YEAR_1, albumRow2.year)
            assertEquals(ARTIST_NAME_1, albumRow2.artists)

            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_ID_0, albumId)
            }
            handle(AlbumsListUserAction.AlbumClicked(ALBUM_ID_0))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateUp>(this@runTest)
            handle(AlbumsListUserAction.UpButtonClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked adds ShowSortBottomSheet event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.ShowSortBottomSheet>(this@runTest) {
                assertEquals(R.string.album_name, sortOption)
                assertEquals(MediaSortOrder.ASCENDING, sortOrder)
            }
            handle(AlbumsListUserAction.SortByClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `MediaSortOptionClicked changes state, adds ScrollToTop event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.YEAR))
            delay(1)
            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)


            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.YEAR))
            delay(1)
            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)

            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.ARTIST))
            delay(1)
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumContextButtonClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(ALBUM_ID_0, albumId)
            }
            handle(AlbumsListUserAction.AlbumContextButtonClicked(albumId = ALBUM_ID_0))
        }
    }

    companion object {
        private const val ALBUM_ID_0 = "0"
        private const val ALBUM_NAME_0 = "ALBUM_NAME_0"
        private const val ALBUM_YEAR_0 = 2000L
        private const val ARTIST_NAME_0 = "ARTIST_NAME_0"

        private const val ALBUM_ID_1 = "1"
        private const val ALBUM_NAME_1 = "ALBUM_NAME_1"
        private const val ARTIST_NAME_1 = "ARTIST_NAME_1"
        private const val ALBUM_YEAR_1 = 1999L
    }
}
