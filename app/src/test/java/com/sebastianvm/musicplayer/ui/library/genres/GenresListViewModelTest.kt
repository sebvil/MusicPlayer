package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.genre
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.sortSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GenresListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var genreRepository: GenreRepository
    private lateinit var preferencesRepository: PreferencesRepository

    @Before
    fun setUp() {
        genreRepository = FakeGenreRepository(genres = listOf(
            genre { genreName = GENRE_NAME_0 },
            genre { genreName = GENRE_NAME_1 }
        ))
        preferencesRepository = FakePreferencesRepository(sortSettings = sortSettings {
            genresListSortSettings = MediaSortOrder.ASCENDING
        })
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                listOf(Genre(genreName = GENRE_NAME_0), Genre(genreName = GENRE_NAME_1)),
                state.value.genresList
            )
        }
    }

    @Test
    fun `GenreClicked adds NavigateToGenre event`() {
        with(generateViewModel()) {
            onGenreClicked(GENRE_NAME_0)
            assertEquals(
                listOf(GenresListUiEvent.NavigateToGenre(genreName = GENRE_NAME_0)),
                events
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(listOf(GenresListUiEvent.NavigateUp), events)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            onSortByClicked()
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(
                listOf(Genre(genreName = GENRE_NAME_1), Genre(genreName = GENRE_NAME_0)),
                state.value.genresList
            )
        }
    }

    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            onGenreOverflowMenuIconClicked(GENRE_NAME_0)
            assertEquals(
                listOf(GenresListUiEvent.OpenContextMenu(genreName = GENRE_NAME_0)),
                events
            )
        }
    }


    companion object {
        private const val GENRE_NAME_0 = "GENRE_NAME_0"
        private const val GENRE_NAME_1 = "GENRE_NAME_1"
    }
}
