//package com.sebastianvm.musicplayer.ui.library.artistlist
//
//import com.sebastianvm.musicplayer.database.entities.C
//import com.sebastianvm.musicplayer.database.entities.Fixtures
//import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
//import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
//import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
//import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
//import com.sebastianvm.musicplayer.ui.components.ArtistRowState
//import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
//import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
//import com.sebastianvm.musicplayer.util.sort.SortPreferences
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//class ArtistListViewModelTest {
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @get:Rule
//    val mainCoroutineRule = DispatcherSetUpRule()
//
//    private lateinit var artistRepository: ArtistRepository
//    private lateinit var preferencesRepository: SortPreferencesRepository
//
//
//    @Before
//    fun setUp() {
//        artistRepository = FakeArtistRepository(
//            artistsWithAlbums = listOf(
//                Fixtures.artistWithAlbumsAna,
//                Fixtures.artistWithAlbumsBob,
//                Fixtures.artistWithAlbumsCamilo
//            )
//        )
//
//    }
//
//    private fun generateViewModel(artistListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING): ArtistListViewModel {
//        preferencesRepository =
//            FakeSortPreferencesRepository(SortPreferences(artistListSortOrder = artistListSortOrder))
//        return ArtistListViewModel(
//            initialState = ArtistListState(
//                artistList = listOf(),
//                sortOrder = MediaSortOrder.DESCENDING,
//            ),
//            artistRepository = artistRepository,
//            preferencesRepository = preferencesRepository,
//        )
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `init sets initial state`() = runTest {
//        with(generateViewModel(MediaSortOrder.ASCENDING)) {
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
//            assertEquals(
//                listOf(
//                    ArtistRowState(
//                        artistId = C.ID_ONE,
//                        artistName = C.ARTIST_ANA,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_TWO,
//                        artistName = C.ARTIST_BOB,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_THREE,
//                        artistName = C.ARTIST_CAMILO,
//                        shouldShowContextMenu = true
//                    ),
//                ),
//                state.value.artistList
//            )
//        }
//
//        with(generateViewModel(MediaSortOrder.DESCENDING)) {
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(
//                listOf(
//                    ArtistRowState(
//                        artistId = C.ID_THREE,
//                        artistName = C.ARTIST_CAMILO,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_TWO,
//                        artistName = C.ARTIST_BOB,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_ONE,
//                        artistName = C.ARTIST_ANA,
//                        shouldShowContextMenu = true
//                    ),
//                ),
//                state.value.artistList
//            )
//        }
//    }
//
//    @Test
//    fun `onArtistClicked adds NavigateToArtist event`() {
//        with(generateViewModel()) {
//            onArtistClicked(C.ID_ONE)
//            assertEquals(listOf(ArtistListUiEvent.NavigateToArtist(C.ID_ONE)), events.value)
//        }
//    }
//
//    @Test
//    fun `onUpButtonClicked adds NavigateUp event`() {
//        with(generateViewModel()) {
//            onUpButtonClicked()
//            assertEquals(listOf(ArtistListUiEvent.NavigateUp), events.value)
//        }
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `SortByClicked changes sortOrder`() = runTest {
//        with(generateViewModel()) {
//            advanceUntilIdle()
//            onSortByClicked()
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(
//                listOf(
//                    ArtistRowState(
//                        artistId = C.ID_THREE,
//                        artistName = C.ARTIST_CAMILO,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_TWO,
//                        artistName = C.ARTIST_BOB,
//                        shouldShowContextMenu = true
//                    ),
//                    ArtistRowState(
//                        artistId = C.ID_ONE,
//                        artistName = C.ARTIST_ANA,
//                        shouldShowContextMenu = true
//                    ),
//                ),
//                state.value.artistList
//            )
//        }
//    }
//
//    @Test
//    fun `ContextMenuIconClicked adds OpenContextMenu event`() {
//        with(generateViewModel()) {
//            onArtistOverflowMenuIconClicked(artistId = C.ID_ONE)
//            assertEquals(
//                listOf(ArtistListUiEvent.OpenContextMenu(artistId = C.ID_ONE)),
//                events.value
//            )
//        }
//    }
//}
