//package com.sebastianvm.musicplayer.ui.library.genrelist
//
//import com.sebastianvm.musicplayer.database.entities.C
//import com.sebastianvm.musicplayer.database.entities.Fixtures
//import com.sebastianvm.musicplayer.database.entities.Genre
//import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
//import com.sebastianvm.musicplayer.repository.genre.GenreRepository
//import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
//import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
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
//@OptIn(ExperimentalCoroutinesApi::class)
//class GenreListViewModelTest {
//
//    @get:Rule
//    val dispatcherSetUpRule = DispatcherSetUpRule()
//
//    private lateinit var genreRepository: GenreRepository
//    private lateinit var preferencesRepository: SortPreferencesRepository
//    private val genres: List<Genre> = listOf(
//        Fixtures.genreAlpha,
//        Fixtures.genreBeta,
//        Fixtures.genreCharlie
//    )
//
//    @Before
//    fun setUp() {
//        genreRepository = FakeGenreRepository(genres)
//
//    }
//
//    private fun generateViewModel(genreListSortOrder: MediaSortOrder = MediaSortOrder.ASCENDING): GenreListViewModel {
//        preferencesRepository =
//            FakeSortPreferencesRepository(SortPreferences(genreListSortOrder = genreListSortOrder))
//        return GenreListViewModel(
//            initialState = GenreListState(
//                genreList = listOf(),
//                sortOrder = MediaSortOrder.DESCENDING,
//            ),
//            genreRepository = genreRepository,
//            preferencesRepository = preferencesRepository,
//        )
//    }
//
//    @Test
//    fun `init sets initial state`() = runTest {
//        with(generateViewModel(genreListSortOrder = MediaSortOrder.ASCENDING)) {
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
//            assertEquals(
//                genres.sortedBy { it.genreName },
//                state.value.genreList
//            )
//        }
//
//        with(generateViewModel(genreListSortOrder = MediaSortOrder.DESCENDING)) {
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(
//                genres.sortedByDescending { it.genreName },
//                state.value.genreList
//            )
//        }
//    }
//
//    @Test
//    fun `GenreClicked adds NavigateToGenre event`() {
//        with(generateViewModel()) {
//            onGenreClicked(genreId = C.ID_ONE)
//            assertEquals(
//                listOf(GenreListUiEvent.NavigateToGenre(genreId = C.ID_ONE)),
//                events.value
//            )
//        }
//    }
//
//    @Test
//    fun `UpButtonClicked adds NavigateUp event`() {
//        with(generateViewModel()) {
//            onUpButtonClicked()
//            assertEquals(listOf(GenreListUiEvent.NavigateUp), events.value)
//        }
//    }
//
//    @Test
//    fun `SortByClicked changes sortOrder`() = runTest {
//        with(generateViewModel()) {
//            advanceUntilIdle()
//            onSortByClicked()
//            advanceUntilIdle()
//            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
//            assertEquals(
//                genres.sortedByDescending { it.genreName },
//                state.value.genreList
//            )
//        }
//    }
//
//    @Test
//    fun `OverflowMenuIconClicked adds OpenContextMenu event`() {
//        with(generateViewModel()) {
//            onGenreOverflowMenuIconClicked(genreId = C.ID_ONE)
//            assertEquals(
//                listOf(GenreListUiEvent.OpenContextMenu(genreId = C.ID_ONE)),
//                events.value
//            )
//        }
//    }
//}
