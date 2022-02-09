package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.genre
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GenresListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var genres: List<Genre>

    @Before
    fun setUp() {
        genres = listOf(
            genre { genreName = GENRE_NAME_0 },
            genre { genreName = GENRE_NAME_1 }
        )
    }

    private fun generateViewModel(): GenresListViewModel {
        return GenresListViewModel(
            initialState = GenresListState(
                genresList = listOf(),
                sortOrder = MediaSortOrder.DESCENDING
            ),
            genreRepository = FakeGenreRepository(),
            preferencesRepository = FakePreferencesRepository(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                listOf<Genre>(),
                state.value.genresList
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GenreClicked adds NavigateToGenre event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateToGenre>(this@runTest) {
                assertEquals(GENRE_NAME_0, genreName)
            }
            handle(GenresListUserAction.GenreClicked(GENRE_NAME_0))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateUp>(this@runTest)
            handle(GenresListUserAction.UpButtonClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            delay(1)
            handle(GenresListUserAction.SortByClicked)
            delay(1)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(genres, state.value.genresList)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(GENRE_NAME_0, genreName)
                assertEquals(MediaSortOption.TRACK, currentSort)
                assertEquals(MediaSortOrder.ASCENDING, sortOrder)
            }
            handle(GenresListUserAction.OverflowMenuIconClicked(GENRE_NAME_0))
        }
    }


    companion object {
        private const val GENRE_NAME_0 = "GENRE_NAME_0"
        private const val GENRE_NAME_1 = "GENRE_NAME_1"
    }
}
