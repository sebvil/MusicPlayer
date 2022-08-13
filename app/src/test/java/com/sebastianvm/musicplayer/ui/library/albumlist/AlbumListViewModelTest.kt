package com.sebastianvm.musicplayer.ui.library.albumlist

import android.provider.MediaStore
import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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


@OptIn(ExperimentalCoroutinesApi::class)
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
            initialState = AlbumListState(
                albumList = listOf(),
            ),
            albumRepository = albumRepository,
            preferencesRepository = preferencesRepository
        )
    }

    private fun TestScope.checkInitialValuesWithInitialSortPreferences(
        initialSortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>,
        expectedAlbums: List<AlbumRowState>
    ) {
        with(generateViewModel(initialSortPreferences = initialSortPreferences)) {
            advanceUntilIdle()
            assertEquals(expectedAlbums, state.value.albumList)

        }
    }

    @Test
    fun `init sets initial state values`() = runTest {
        val albums = listOf(albumRowAlpaca, albumRowBobcat, albumRowCheetah)
        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                SortOptions.AlbumListSortOptions.ALBUM,
                MediaSortOrder.ASCENDING
            ),
            albums
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.albumName }
        )
        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            albums.sortedBy { it.year }
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.year }
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            albums.sortedByDescending { it.artists }
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            albums.sortedBy { it.artists }
        )
    }

    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.AlbumClicked(C.ID_ONE))
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.Album(
                        arguments = AlbumArguments(
                            albumId = C.ID_ONE
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.UpButtonClicked)
            assertEquals(navEvents.value.first(), NavEvent.NavigateUp)
        }
    }

    @Test
    fun `SortByClicked adds ShowSortBottomSheet event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.SortByClicked)
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.SortMenu(
                        arguments = SortMenuArguments(
                            listType = SortableListType.Albums
                        )
                    )
                )
            )
        }
    }


    @Test
    fun `onAlbumOverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            handle(AlbumListUserAction.AlbumOverflowIconClicked(albumId = C.ID_ONE))
            assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.AlbumContextMenu(
                        AlbumContextMenuArguments(albumId = C.ID_ONE)
                    )
                )
            )
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

    private suspend fun TestScope.checkStateWhenSortPrefsModified(
        viewModel: AlbumListViewModel,
        sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>,
        expectedAlbums: List<AlbumRowState>
    ) {
        with(viewModel) {
            preferencesRepository.modifyAlbumListSortPreferences(sortPreferences)
            advanceUntilIdle()
            assertEquals(expectedAlbums, state.value.albumList)
        }

    }

    companion object {
        private val albumRowAlpaca = AlbumRowState(
            albumId = C.ID_ONE,
            albumName = C.ALBUM_ALPACA,
            year = C.YEAR_2021,
            imageUri = "${MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI}/${C.ID_ONE}",
            artists = C.ARTIST_CAMILO
        )

        private val albumRowBobcat = AlbumRowState(
            albumId = C.ID_TWO,
            albumName = C.ALBUM_BOBCAT,
            imageUri = "${MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI}/${C.ID_TWO}",
            year = C.YEAR_2022,
            artists = C.ARTIST_ANA
        )

        private val albumRowCheetah = AlbumRowState(
            albumId = C.ID_THREE,
            albumName = C.ALBUM_CHEETAH,
            imageUri = "${MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI}/${C.ID_THREE}",
            year = C.YEAR_2020,
            artists = C.ARTIST_BOB
        )

    }
}
