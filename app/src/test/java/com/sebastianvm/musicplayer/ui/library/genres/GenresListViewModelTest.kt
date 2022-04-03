package com.sebastianvm.musicplayer.ui.library.genres

import androidx.test.core.app.ApplicationProvider
import com.sebastianvm.musicplayer.database.daos.FakeGenreDao
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.database.entities.genre
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.util.mvvm.updateState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GenresListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var genreRepository: GenreRepository
    private lateinit var preferencesRepository: SortPreferencesRepositoryImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        genreRepository = GenreRepository(
            FakeGenreDao(genres = listOf(
                genre { genreName = GENRE_NAME_0 },
                genre { genreName = GENRE_NAME_1 }
            )))

        preferencesRepository = SortPreferencesRepositoryImpl(
            context = ApplicationProvider.getApplicationContext(),
            ioDispatcher = Dispatchers.Main
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun generateViewModel(): GenresListViewModel {
        return GenresListViewModel(
            initialState = GenresListState(
                genresList = listOf(),
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            genreRepository = genreRepository,
            preferencesRepository = preferencesRepository,
            ioDispatcher = Dispatchers.Main
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            updateState()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            updateState()
            onSortByClicked()
            updateState()
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
                events.value
            )
        }
    }


    companion object {
        private const val GENRE_NAME_0 = "GENRE_NAME_0"
        private const val GENRE_NAME_1 = "GENRE_NAME_1"
    }
}
