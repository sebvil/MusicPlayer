package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.fullAlbumInfo
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import com.sebastianvm.musicplayer.util.sort.sortSettings
import com.sebastianvm.musicplayer.util.uri.FakeUriUtilsRule
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

    @get:Rule
    val fakeUriUtilsRule = FakeUriUtilsRule()

    private lateinit var albumRepository: AlbumRepository
    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    fun setUp() {
        preferencesRepository = FakePreferencesRepository(sortSettings = sortSettings {
            albumsListSortSettings = mediaSortSettings { sortOption = MediaSortOption.ALBUM }
        })
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_ID_0, albumId)
            }
            onAlbumClicked(ALBUM_ID_0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateUp>(this@runTest)
            onUpButtonClicked()
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
            onSortByClicked()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumContextButtonClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(ALBUM_ID_0, albumId)
            }
            onAlbumOverflowMenuIconClicked(albumId = ALBUM_ID_0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `modifying sortOption changes order`() = runTest {
        with(generateViewModel()) {
            preferencesRepository.modifyAlbumsListSortOptions(mediaSortSettings {
                sortOption = MediaSortOption.ALBUM
                sortOrder = MediaSortOrder.DESCENDING
            })
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortOptions(mediaSortSettings {
                sortOption = MediaSortOption.ARTIST
                sortOrder = MediaSortOrder.DESCENDING
            })
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortOptions(mediaSortSettings {
                sortOption = MediaSortOption.ARTIST
                sortOrder = MediaSortOrder.ASCENDING
            })
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_0, ALBUM_ROW_1), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortOptions(mediaSortSettings {
                sortOption = MediaSortOption.YEAR
                sortOrder = MediaSortOrder.ASCENDING
            })
            assertEquals(MediaSortOption.YEAR, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(listOf(ALBUM_ROW_1, ALBUM_ROW_0), state.value.albumsList)

            preferencesRepository.modifyAlbumsListSortOptions(mediaSortSettings {
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
            imageUri = "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${ALBUM_ID_0}",
            artists = ARTIST_NAME_0
        )

        private val ALBUM_ROW_1 = AlbumRowState(
            albumId = ALBUM_ID_1,
            albumName = ALBUM_NAME_1,
            year = ALBUM_YEAR_1,
            imageUri = "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${ALBUM_ID_1}",
            artists = ARTIST_NAME_1
        )
    }
}
