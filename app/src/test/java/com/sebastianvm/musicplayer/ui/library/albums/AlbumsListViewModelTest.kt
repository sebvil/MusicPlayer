package com.sebastianvm.musicplayer.ui.library.albums

import android.content.ContentUris
import android.provider.MediaStore
import com.sebastianvm.commons.R
import com.sebastianvm.musicplayer.database.entities.fullAlbumInfo
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
                fullAlbumInfo {
                    album {
                        albumId = ALBUM_ID_0
                        albumName = ALBUM_NAME_0
                        year = ALBUM_YEAR_0
                        artists = ARTIST_NAME_0
                    }
                },
                fullAlbumInfo {
                    album {
                        albumId = ALBUM_ID_1
                        albumName = ALBUM_NAME_1
                        year = ALBUM_YEAR_1
                        artists = ARTIST_NAME_1
                    }
                })
        )
    }

    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(
            initialState = AlbumsListState(
                albumsList = listOf(),
                currentSort = MediaSortOption.YEAR,
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            albumRepository = albumRepository,
            preferencesRepository = preferencesRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state values`() = runTest {
        with(generateViewModel()) {
            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)

        }
    }

    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() {
        with(generateViewModel()) {
            onAlbumClicked(ALBUM_ID_0)
            assertEquals(
                listOf(AlbumsListUiEvent.NavigateToAlbum(albumId = ALBUM_ID_0)),
                events
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(listOf(AlbumsListUiEvent.NavigateUp), events)
        }
    }

    @Test
    fun `SortByClicked adds ShowSortBottomSheet event`() {
        with(generateViewModel()) {
            onSortByClicked()
            assertEquals(
                listOf(
                    AlbumsListUiEvent.ShowSortBottomSheet(
                        sortOption = R.string.album_name,
                        sortOrder = MediaSortOrder.ASCENDING
                    )
                ),
                events
            )
        }
    }

    // TODO move this to sortBy vm
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `MediaSortOptionClicked changes state, adds ScrollToTop event`() = runTest {
//        with(generateViewModel()) {
//            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
//            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.YEAR))
//            delay(1)
//            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
//            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
//
//
//            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
//            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.YEAR))
//            delay(1)
//            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//
//            expectUiEvent<AlbumsListUiEvent.ScrollToTop>(this@runTest)
//            handle(AlbumsListUserAction.MediaSortOptionClicked(MediaSortOption.ARTIST))
//            delay(1)
//            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//        }
//    }

    @Test
    fun `AlbumContextButtonClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            onAlbumOverflowMenuIconClicked(albumId = ALBUM_ID_0)
            assertEquals(
                listOf(AlbumsListUiEvent.OpenContextMenu(albumId = ALBUM_ID_0)),
                events
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `modifying sortOption changes order`() = runTest {
        with(generateViewModel()) {
            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
                sortOption = MediaSortOption.ALBUM
                sortOrder = MediaSortOrder.DESCENDING
            })
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
                sortOption = MediaSortOption.ARTIST
                sortOrder = MediaSortOrder.DESCENDING
            })
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
                sortOption = MediaSortOption.ARTIST
                sortOrder = MediaSortOrder.ASCENDING
            })
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
                sortOption = MediaSortOption.YEAR
                sortOrder = MediaSortOrder.ASCENDING
            })
            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortPreferences(mediaSortSettings {
                sortOption = MediaSortOption.YEAR
                sortOrder = MediaSortOrder.DESCENDING
            })
            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)

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

        private val ALBUM_ROW_0 = AlbumRowState(
            albumId = ALBUM_ID_0,
            albumName = ALBUM_NAME_0,
            year = ALBUM_YEAR_0,
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                ALBUM_ID_0.toLong()
            ),
            artists = ARTIST_NAME_0
        )

        private val ALBUM_ROW_1 = AlbumRowState(
            albumId = ALBUM_ID_1,
            albumName = ALBUM_NAME_1,
            year = ALBUM_YEAR_1,
            imageUri = ContentUris.withAppendedId(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                ALBUM_ID_1.toLong()
            ),
            artists = ARTIST_NAME_1
        )
    }
}
