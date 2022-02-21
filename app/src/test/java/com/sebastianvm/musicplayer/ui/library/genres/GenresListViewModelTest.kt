package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.genre
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
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
                sortOrder = MediaSortOrder.DESCENDING
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GenreClicked adds NavigateToGenre event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateToGenre>(this@runTest) {
                assertEquals(GENRE_NAME_0, genreName)
            }
            onGenreClicked(GENRE_NAME_0)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateUp>(this@runTest)
            onUpButtonClicked()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(GENRE_NAME_0, genreName)
            }
            onGenreOverflowMenuIconClicked(GENRE_NAME_0)
        }
    }


    companion object {
        private const val GENRE_NAME_0 = "GENRE_NAME_0"
        private const val GENRE_NAME_1 = "GENRE_NAME_1"
    }
}
