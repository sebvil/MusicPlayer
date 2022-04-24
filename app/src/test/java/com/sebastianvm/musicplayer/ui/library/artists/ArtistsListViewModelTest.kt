package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.database.entities.artistWithAlbums
import com.sebastianvm.musicplayer.repository.playback.mediaqueue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ArtistsListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    private lateinit var artistRepository: MediaQueueRepository
    private lateinit var preferencesRepository: SortPreferencesRepository


    @Before
    fun setUp() {
        artistRepository = FakeArtistRepository(artistsWithAlbums = listOf(
            artistWithAlbums {
                artist {
                    artistName = ARTIST_NAME_0
                }
            },
            artistWithAlbums {
                artist {
                    artistName = ARTIST_NAME_1
                }
            }
        ))

        preferencesRepository = mockk()
    }

    private fun generateViewModel(): ArtistsListViewModel {
        return ArtistsListViewModel(
            initialState = ArtistsListState(
                artistsList = listOf(),
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            artistRepository = artistRepository,
            preferencesRepository = preferencesRepository,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() = runTest {
        with(generateViewModel()) {
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(
                listOf(
                    ArtistRowState(
                        artistName = ARTIST_NAME_0,
                        shouldShowContextMenu = true
                    ),
                    ArtistRowState(
                        artistName = ARTIST_NAME_1,
                        shouldShowContextMenu = true
                    )
                ),
                state.value.artistsList
            )
        }
    }

    @Test
    fun `onArtistClicked adds NavigateToArtist event`() {
        with(generateViewModel()) {
            onArtistClicked(ARTIST_NAME_0)
            assertEquals(listOf(ArtistsListUiEvent.NavigateToArtist(ARTIST_NAME_0)), events)
        }
    }

    @Test
    fun `onUpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(listOf(ArtistsListUiEvent.NavigateUp), events)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes sortOrder`() = runTest {
        with(generateViewModel()) {
            onSortByClicked()
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(
                listOf(
                    ArtistRowState(
                        artistName = ARTIST_NAME_1,
                        shouldShowContextMenu = true
                    ),
                    ArtistRowState(
                        artistName = ARTIST_NAME_0,
                        shouldShowContextMenu = true
                    )
                ),
                state.value.artistsList
            )
        }
    }

    @Test
    fun `ContextMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            onArtistOverflowMenuIconClicked(artistName = ARTIST_NAME_0)
            assertEquals(
                listOf(ArtistsListUiEvent.OpenContextMenu(artistName = ARTIST_NAME_0)),
                events
            )
        }
    }

    companion object {
        private const val ARTIST_NAME_0 = "ARTIST_NAME_0"
        private const val ARTIST_NAME_1 = "ARTIST_NAME_1"
    }
}
