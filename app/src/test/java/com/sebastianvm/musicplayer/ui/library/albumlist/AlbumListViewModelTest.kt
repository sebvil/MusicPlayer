package com.sebastianvm.musicplayer.ui.library.albumlist

import com.sebastianvm.musicplayer.R
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
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
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
        expectedAlbums: List<ModelListItemState>
    ) {
        with(generateViewModel(initialSortPreferences = initialSortPreferences)) {
            advanceUntilIdle()
            assertEquals(expectedAlbums, state.value.albumList)

        }
    }

    @Test
    fun `init sets initial state values`() = runTest {
        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                SortOptions.AlbumListSortOptions.ALBUM,
                MediaSortOrder.ASCENDING
            ),
            listOf(albumRowAlpaca, albumRowBobcat, albumRowCheetah)
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            listOf(albumRowCheetah, albumRowBobcat, albumRowAlpaca)
        )
        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            listOf(albumRowCheetah, albumRowAlpaca, albumRowBobcat)
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.YEAR,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            listOf(albumRowBobcat, albumRowAlpaca, albumRowCheetah)
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            listOf(albumRowAlpaca, albumRowCheetah, albumRowBobcat)
        )

        checkInitialValuesWithInitialSortPreferences(
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                sortOrder = MediaSortOrder.ASCENDING
            ),
            listOf(albumRowBobcat, albumRowCheetah, albumRowAlpaca)
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
        with(generateViewModel()) {
            advanceUntilIdle()
            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                listOf(albumRowCheetah, albumRowBobcat, albumRowAlpaca)
            )
            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.YEAR,
                    sortOrder = MediaSortOrder.ASCENDING
                ),
                listOf(albumRowCheetah, albumRowAlpaca, albumRowBobcat)
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.YEAR,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                listOf(albumRowBobcat, albumRowAlpaca, albumRowCheetah)
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                    sortOrder = MediaSortOrder.DESCENDING
                ),
                listOf(albumRowAlpaca, albumRowCheetah, albumRowBobcat)
            )

            checkStateWhenSortPrefsModified(
                this,
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ARTIST,
                    sortOrder = MediaSortOrder.ASCENDING
                ),
                listOf(albumRowBobcat, albumRowCheetah, albumRowAlpaca)
            )

        }
    }

    private suspend fun TestScope.checkStateWhenSortPrefsModified(
        viewModel: AlbumListViewModel,
        sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>,
        expectedAlbums: List<ModelListItemState>
    ) {
        with(viewModel) {
            preferencesRepository.modifyAlbumListSortPreferences(sortPreferences)
            advanceUntilIdle()
            assertEquals(expectedAlbums, state.value.albumList)
        }

    }

    companion object {
        private val albumRowAlpaca = ModelListItemState(
            id = C.ID_ONE,
            headlineText = C.ALBUM_ALPACA,
            supportingText = "${C.YEAR_2021} ${C.ARTIST_CAMILO}",
            mediaArtImageState = MediaArtImageState(
                imageUri = C.IMAGE_URI_1,
                contentDescription = R.string.album_art_for_album,
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                args = listOf(C.ALBUM_ALPACA)
            )
        )


        private val albumRowBobcat = ModelListItemState(
            id = C.ID_TWO,
            headlineText = C.ALBUM_BOBCAT,
            supportingText = "${C.YEAR_2022} ${C.ARTIST_ANA}",
            mediaArtImageState = MediaArtImageState(
                imageUri = C.IMAGE_URI_2,
                contentDescription = R.string.album_art_for_album,
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                args = listOf(C.ALBUM_BOBCAT)
            )
        )


        private val albumRowCheetah = ModelListItemState(
            id = C.ID_THREE,
            headlineText = C.ALBUM_CHEETAH,
            supportingText = "${C.YEAR_2020} ${C.ARTIST_BOB}",
            mediaArtImageState = MediaArtImageState(
                imageUri = C.IMAGE_URI_3,
                contentDescription = R.string.album_art_for_album,
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                args = listOf(C.ALBUM_CHEETAH)
            )
        )

    }
}
