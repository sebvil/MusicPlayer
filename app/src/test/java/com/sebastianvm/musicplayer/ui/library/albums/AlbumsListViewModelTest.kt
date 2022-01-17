package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AlbumsListViewModelTest {

    // TODO fix this so I don't need to use robolectric
//    @get:Rule
//    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(
            initialState = AlbumsListState(
                albumsList = listOf(),
                currentSort = SortOption.YEAR,
                sortOrder = SortOrder.DESCENDING,
            ),
            albumRepository = FakeAlbumRepository(),
            preferencesRepository = FakePreferencesRepository()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state values`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(2, state.value.albumsList.size)
                val albumRow1 = state.value.albumsList[0]
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumRow1.albumId)
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_NAME, albumRow1.albumName)
                assertEquals(AlbumBuilder.DEFAULT_YEAR, albumRow1.year)
                assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, albumRow1.artists)

                val albumRow2 = state.value.albumsList[1]
                assertEquals(AlbumBuilder.SECONDARY_ALBUM_ID, albumRow2.albumId)
                assertEquals(AlbumBuilder.SECONDARY_ALBUM_NAME, albumRow2.albumName)
                assertEquals(AlbumBuilder.SECONDARY_YEAR, albumRow2.year)
                assertEquals(ArtistBuilder.SECONDARY_ARTIST_NAME, albumRow2.artists)

                assertEquals(SortOption.ALBUM_NAME, state.value.currentSort)
                assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            }
            delay(1)
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
}
