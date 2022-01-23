package com.sebastianvm.musicplayer.ui.library.genres

import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class GenresListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(): GenresListViewModel {
        return GenresListViewModel(
            initialState = GenresListState(genresList = listOf(), sortOrder = SortOrder.DESCENDING),
            genreRepository = mock(),
            preferencesRepository = FakePreferencesRepository(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `GenreClicked adds NavigateToGenre event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<GenresListUiEvent.NavigateToGenre>(this@runTest) {
                Assert.assertEquals(GENRE_NAME, genreName)
            }
            handle(GenresListUserAction.GenreClicked(GENRE_NAME))
        }
    }

    companion object {
        private const val GENRE_NAME = "GENRE_NAME"
    }
}
