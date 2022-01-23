package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArtistsListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()


    private fun generateViewModel(): ArtistsListViewModel {
        return ArtistsListViewModel(
            initialState = ArtistsListState(
                artistsList = listOf(),
                sortOrder = SortOrder.DESCENDING
            ),
            artistRepository = FakeArtistRepository(),
            preferencesRepository = FakePreferencesRepository(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(1, state.value.artistsList.size)
            val artistRowState = state.value.artistsList[0]
            assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, artistRowState.artistName)
            assertTrue(artistRowState.shouldShowContextMenu)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ArtistClicked adds NavigateToArtist event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistsListUiEvent.NavigateToArtist>(this@runTest) {
                assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, artistName)
            }
            handle(ArtistsListUserAction.ArtistClicked(ArtistBuilder.DEFAULT_ARTIST_NAME))
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistsListUiEvent.NavigateUp>(this@runTest)
            handle(ArtistsListUserAction.UpButtonClicked)
        }
    }

    @Test
    fun `SortByClicked changes sortOrder`() {
        with(generateViewModel()) {
            handle(ArtistsListUserAction.SortByClicked)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ContextMenuIconClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistsListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(ArtistBuilder.DEFAULT_ARTIST_NAME, artistName)
            }
            handle(ArtistsListUserAction.ContextMenuIconClicked(artistName = ArtistBuilder.DEFAULT_ARTIST_NAME))
        }
    }
}
