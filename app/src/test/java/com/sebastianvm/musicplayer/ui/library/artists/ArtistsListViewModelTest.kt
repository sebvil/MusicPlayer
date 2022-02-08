package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
                sortOrder = MediaSortOrder.DESCENDING
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
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                listOf(
                    ArtistRowState(
                        artistName = ArtistBuilder.DEFAULT_ARTIST_NAME,
                        shouldShowContextMenu = true
                    ),
                    ArtistRowState(
                        artistName = ArtistBuilder.SECONDARY_ARTIST_NAME,
                        shouldShowContextMenu = true
                    )
                ), state.value.artistsList
            )

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
    fun `UpDEFAULT_ALBUM_IDButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistsListUiEvent.NavigateUp>(this@runTest)
            handle(ArtistsListUserAction.UpButtonClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            delay(1)
            handle(ArtistsListUserAction.SortByClicked)
            delay(1)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
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
