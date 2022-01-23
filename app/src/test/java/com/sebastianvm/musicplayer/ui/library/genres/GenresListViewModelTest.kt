package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GenresListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(): GenresListViewModel {
        return GenresListViewModel(
            initialState = GenresListState(genresList = listOf(), sortOrder = SortOrder.DESCENDING),
            genreRepository = FakeGenreRepository(),
            preferencesRepository = FakePreferencesRepository(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                listOf(
                    GenreBuilder.getDefaultGenre().build(),
                    GenreBuilder.getSecondaryGenre().build()
                ),
                state.value.genresList
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GenreClicked adds NavigateToGenre event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateToGenre>(this@runTest) {
                assertEquals(GENRE_NAME, genreName)
            }
            handle(GenresListUserAction.GenreClicked(GENRE_NAME))
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
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(
                listOf(
                    GenreBuilder.getSecondaryGenre().build(),
                    GenreBuilder.getDefaultGenre().build()
                ), state.value.genresList
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(GenreBuilder.PRIMARY_GENRE_NAME, genreName)
                assertEquals(SortOption.TRACK_NAME, currentSort)
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
            handle(GenresListUserAction.OverflowMenuIconClicked(GenreBuilder.PRIMARY_GENRE_NAME))
        }
    }


    companion object {
        private const val GENRE_NAME = "GENRE_NAME"
    }
}
