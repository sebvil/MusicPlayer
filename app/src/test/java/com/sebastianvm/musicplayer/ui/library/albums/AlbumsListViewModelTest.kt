package com.sebastianvm.musicplayer.ui.library.albums

import android.content.ContentUris
import android.provider.MediaStore
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.fullAlbumAlpaca
import com.sebastianvm.musicplayer.database.entities.fullAlbumBobcat
import com.sebastianvm.musicplayer.database.entities.fullAlbumCheetah
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class AlbumsListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    private lateinit var albumRepository: AlbumRepository
    private lateinit var preferencesRepository: SortPreferencesRepository

    @Before
    fun setUp() {
        preferencesRepository = mockk()
        albumRepository = FakeAlbumRepository(
            fullAlbumInfo = listOf(
                fullAlbumAlpaca,
                fullAlbumBobcat,
                fullAlbumCheetah,
            )
        )
    }

    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state values`() = runTest {
        with(generateViewModel()) {
            assertEquals(
                listOf(albumRowAlpaca, albumRowBobcat, albumRowCheetah),
                state.value.albumsList
            )
            assertEquals(
                MediaSortPreferences(
                    SortOptions.AlbumListSortOptions.ALBUM,
                    MediaSortOrder.ASCENDING
                ), state.value.albumsList
            )

        }
    }

//    @Test
//    fun `AlbumClicked adds NavigateToAlbum event`() {
//        with(generateViewModel()) {
//            onAlbumClicked(ALBUM_ID_0)
//            assertEquals(
//                listOf(AlbumsListUiEvent.NavigateToAlbum(albumId = ALBUM_ID_0)),
//                events
//            )
//        }
//    }
//
//    @Test
//    fun `UpButtonClicked adds NavigateUp event`() {
//        with(generateViewModel()) {
//            onUpButtonClicked()
//            assertEquals(listOf(AlbumsListUiEvent.NavigateUp), events)
//        }
//    }
//
//    @Test
//    fun `SortByClicked adds ShowSortBottomSheet event`() {
//        with(generateViewModel()) {
//            onSortByClicked()
//            assertEquals(
//                listOf(
//                    AlbumsListUiEvent.ShowSortBottomSheet(
//                        sortOption = R.string.album_name,
//                        sortOrder = MediaSortOrder.ASCENDING
//                    )
//                ),
//                events
//            )
//        }
//    }


//    @Test
//    fun `AlbumContextButtonClicked adds OpenContextMenu event`() {
//        with(generateViewModel()) {
//            onAlbumOverflowMenuIconClicked(albumId = ALBUM_ID_0)
//            assertEquals(
//                listOf(AlbumsListUiEvent.OpenContextMenu(albumId = ALBUM_ID_0)),
//                events
//            )
//        }
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `modifying sortOption changes order`() = runTest {
//        with(generateViewModel()) {
//            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
//                sortOption = MediaSortOption.ALBUM
//                sortOrder = MediaSortOrder.DESCENDING
//            })
//            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)
//
//            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
//                sortOption = MediaSortOption.ARTIST
//                sortOrder = MediaSortOrder.DESCENDING
//            })
//            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)
//
//            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
//                sortOption = MediaSortOption.ARTIST
//                sortOrder = MediaSortOrder.ASCENDING
//            })
//            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
//            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
//            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)
//
//            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
//                sortOption = MediaSortOption.YEAR
//                sortOrder = MediaSortOrder.ASCENDING
//            })
//            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
//            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
//            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)
//
//            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
//                sortOption = MediaSortOption.YEAR
//                sortOrder = MediaSortOrder.DESCENDING
//            })
//            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)
//
//        }
//    }

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
