package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.genreFixtureList
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.Fixtures
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GenresListViewModelTest {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var genreRepository: GenreRepository
    private lateinit var preferencesRepository: SortPreferencesRepository
    private lateinit var genres: List<Genre>

    @Before
    fun setUp() {
        genres = genreFixtureList(10)
        genreRepository = FakeGenreRepository(genres)
        preferencesRepository = FakeSortPreferencesRepository()

    }

    private fun generateViewModel(): GenresListViewModel {
        return GenresListViewModel(
            initialState = GenresListState(
                genresList = listOf(),
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            genreRepository = genreRepository,
            preferencesRepository = preferencesRepository,
        )
    }

    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                genres.sortedWith(getStringComparator(MediaSortOrder.ASCENDING) { it.genreName }),
                state.value.genresList
            )
        }
    }

    @Test
    fun `GenreClicked adds NavigateToGenre event`() {
        val genreName = Fixtures.getRandomString()
        with(generateViewModel()) {
            onGenreClicked(genreName = genreName)
            assertEquals(
                listOf(GenresListUiEvent.NavigateToGenre(genreName = genreName)),
                events.value
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(listOf(GenresListUiEvent.NavigateUp), events.value)
        }
    }

    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            onSortByClicked()
            advanceUntilIdle()
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(
                genres.sortedWith(getStringComparator(MediaSortOrder.DESCENDING) { it.genreName }),
                state.value.genresList
            )
        }
    }

    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() {
        val genreName = Fixtures.getRandomString()
        with(generateViewModel()) {
            onGenreOverflowMenuIconClicked(genreName)
            assertEquals(
                listOf(GenresListUiEvent.OpenContextMenu(genreName = genreName)),
                events.value
            )
        }
    }
}
