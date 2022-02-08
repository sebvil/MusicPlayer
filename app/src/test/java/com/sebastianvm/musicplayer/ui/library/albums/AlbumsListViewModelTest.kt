package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
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
            assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumRow1.albumId)
            assertEquals(AlbumBuilder.DEFAULT_ALBUM_NAME, albumRow1.albumName)
            assertEquals(
                "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${AlbumBuilder.DEFAULT_ALBUM_ID}",
                albumRow1.imageUri
            )
            assertEquals(AlbumBuilder.DEFAULT_YEAR, albumRow1.year)
            assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, albumRow1.artists)

            val albumRow2 = state.value.albumsList[1]
            assertEquals(AlbumBuilder.SECONDARY_ALBUM_ID, albumRow2.albumId)
            assertEquals(AlbumBuilder.SECONDARY_ALBUM_NAME, albumRow2.albumName)
            assertEquals(
                "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${AlbumBuilder.SECONDARY_ALBUM_ID}",
                albumRow2.imageUri
            )
            assertEquals(AlbumBuilder.SECONDARY_YEAR, albumRow2.year)
            assertEquals(ArtistBuilder.SECONDARY_ARTIST_NAME, albumRow2.artists)

            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumId)
            }
            handle(AlbumsListUserAction.AlbumClicked(AlbumBuilder.DEFAULT_ALBUM_ID))
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
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumId)
            }
            handle(AlbumsListUserAction.AlbumContextButtonClicked(albumId = AlbumBuilder.DEFAULT_ALBUM_ID))
        }
    }
}
