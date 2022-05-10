package com.sebastianvm.musicplayer.ui.library.albumlist

import android.content.ContentUris
import android.provider.MediaStore
import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.SortPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AlbumListViewModelTest {

    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    private lateinit var albumRepository: AlbumRepository
    private lateinit var preferencesRepository: SortPreferencesRepository

    @Before
    fun setUp() {
        albumRepository = FakeAlbumRepository(
            fullAlbumInfo = listOf(
                Fixtures.fullAlbumAlpaca,
                Fixtures.fullAlbumBobcat,
                Fixtures.fullAlbumCheetah,
            )
        )
    }

    private fun generateViewModel(
        initialSortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions> = MediaSortPreferences(
            SortOptions.AlbumListSortOptions.ALBUM,
            MediaSortOrder.ASCENDING
        )
    ): AlbumListViewModel {
        preferencesRepository =
            FakeSortPreferencesRepository(SortPreferences(albumListSortPreferences = initialSortPreferences))

        return AlbumListViewModel(
            initialState = AlbumsListState(
                albumsList = listOf(),
                sortPreferences = MediaSortPreferences(
                    SortOptions.AlbumListSortOptions.ALBUM,
                    MediaSortOrder.ASCENDING
                )
            ),
            albumRepository = albumRepository,
            preferencesRepository = preferencesRepository
        )
    }

    private fun TestScope.verifyInitialValuesWithInitialSortPreferences(
        initialSortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>,
        expectedAlbums: List<AlbumRowState>
    ) {
        with(generateViewModel(initialSortPreferences = initialSortPreferences)) {
            advanceUntilIdle()
            assertEquals(expectedAlbums, state.value.albumsList)
            assertEquals(initialSortPreferences, state.value.sortPreferences)

        }
    }

    @Test
    fun `init sets initial state values`() = runTest {
        val albums = listOf(albumRowAlpaca, albumRowBobcat, albumRowCheetah)
        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                SortOptions.AlbumListSortOptions.ALBUM,
                MediaSortOrder.ASCENDING
            ),
            albums
        )

        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.albumName }
        )
        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            albums.sortedBy { it.year }
        )

        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.year }
        )

        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.artists }
        )

        verifyInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            albums.sortedBy { it.artists }
        )
    }

    @Test
    fun `onAlbumClicked adds NavigateToAlbum event`() {
        with(generateViewModel()) {
            onAlbumClicked(C.ID_ONE)
            assertEquals(
                listOf(AlbumsListUiEvent.NavigateToAlbum(albumId = C.ID_ONE)),
                events.value,
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(
                listOf(AlbumsListUiEvent.NavigateUp),
                events.value,
            )
        }
    }

    @Test
    fun `SortByClicked adds ShowSortBottomSheet event`() {
        with(generateViewModel()) {
            onSortByClicked()
            assertEquals(listOf(AlbumsListUiEvent.ShowSortBottomSheet), events.value)
        }
    }


    @Test
    fun `onAlbumOverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            onAlbumOverflowMenuIconClicked(albumId = C.ID_ONE)
            assertEquals(
                listOf(AlbumsListUiEvent.OpenContextMenu(albumId = C.ID_ONE)),
                events.value
            )
        }
    }

    private suspend fun TestScope.checkStateWhenSortPrefsModified(
        viewModel: AlbumListViewModel,
        sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>,
        expectedAlbums: List<AlbumRowState>
    ) {
        with(viewModel) {
            preferencesRepository.modifyAlbumsListSortPreferences(sortPreferences)
            advanceUntilIdle()
            assertEquals(sortPreferences, state.value.sortPreferences)
            assertEquals(expectedAlbums, state.value.albumsList)
        }

    }

    @Test
    fun `modifying sortOption changes order`() = runTest {
        val albums = listOf(albumRowAlpaca, albumRowBobcat, albumRowCheetah)
        with(generateViewModel()) {
            advanceUntilIdle()
            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                albums.sortedByDescending { it.albumName }
            )
            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.YEAR,
                    sortOrder = MediaSortOrder.ASCENDING
                ),
                albums.sortedBy { it.year }
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.YEAR,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                albums.sortedByDescending { it.year }
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                albums.sortedByDescending { it.artists }
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                    sortOrder = MediaSortOrder.ASCENDING
                ),
                albums.sortedBy { it.artists }
            )

        }
    }

    companion object {
        private val albumRowAlpaca = AlbumRowState(
            albumId = C.ID_ONE,
            albumName = C.ALBUM_ALPACA,
            year = C.YEAR_2021,
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                C.ID_ONE
            ),
            artists = C.ARTIST_CAMILO
        )

        private val albumRowBobcat = AlbumRowState(
            albumId = C.ID_TWO,
            albumName = C.ALBUM_BOBCAT,
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                C.ID_TWO
            ),
            year = C.YEAR_2022,
            artists = C.ARTIST_ANA
        )

        private val albumRowCheetah = AlbumRowState(
            albumId = C.ID_THREE,
            albumName = C.ALBUM_CHEETAH,
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                C.ID_THREE
            ),
            year = C.YEAR_2020,
            artists = C.ARTIST_BOB
        )

    }
}
