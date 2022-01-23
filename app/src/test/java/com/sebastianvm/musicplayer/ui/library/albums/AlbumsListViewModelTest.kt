package com.sebastianvm.musicplayer.ui.library.albums

import android.content.ContentUris
import android.net.Uri
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlbumsListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    private lateinit var defaultUri: Uri
    private lateinit var secondaryUri: Uri

    @Before
    fun setUp() {
        mockkStatic(ContentUris::class)
        defaultUri = mockk()
        secondaryUri = mockk()
        every {
            ContentUris.withAppendedId(any(), AlbumBuilder.DEFAULT_ALBUM_ID.toLong())
        } returns defaultUri
        every {
            ContentUris.withAppendedId(any(), AlbumBuilder.SECONDARY_ALBUM_ID.toLong())
        } returns secondaryUri
    }

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
            delay(1)
            assertEquals(2, state.value.albumsList.size)
            val albumRow1 = state.value.albumsList[0]
            assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumRow1.albumId)
            assertEquals(AlbumBuilder.DEFAULT_ALBUM_NAME, albumRow1.albumName)
            assertEquals(defaultUri, albumRow1.imageUri)
            assertEquals(AlbumBuilder.DEFAULT_YEAR, albumRow1.year)
            assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, albumRow1.artists)

            val albumRow2 = state.value.albumsList[1]
            assertEquals(AlbumBuilder.SECONDARY_ALBUM_ID, albumRow2.albumId)
            assertEquals(AlbumBuilder.SECONDARY_ALBUM_NAME, albumRow2.albumName)
            assertEquals(secondaryUri, albumRow2.imageUri)
            assertEquals(AlbumBuilder.SECONDARY_YEAR, albumRow2.year)
            assertEquals(ArtistBuilder.SECONDARY_ARTIST_NAME, albumRow2.artists)

            assertEquals(SortOption.ALBUM_NAME, state.value.currentSort)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)

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
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
            handle(AlbumsListUserAction.SortByClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortOptionClicked changes state, adds ScrollToTop event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.SortOptionClicked(SortOption.YEAR))
            delay(1)
            assertEquals(SortOption.YEAR, state.value.currentSort)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)


            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.SortOptionClicked(SortOption.YEAR))
            delay(1)
            assertEquals(SortOption.YEAR, state.value.currentSort)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)

            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
            handle(AlbumsListUserAction.SortOptionClicked(SortOption.ARTIST_NAME))
            delay(1)
            assertEquals(SortOption.ARTIST_NAME, state.value.currentSort)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
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
