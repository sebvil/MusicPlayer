package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.database.entities.artistWithAlbums
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.sortSettings
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

    private lateinit var artistRepository: ArtistRepository
    private lateinit var preferencesRepository: PreferencesRepository

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

        preferencesRepository = FakePreferencesRepository(sortSettings = sortSettings {
            artistListSortSettings = MediaSortOrder.ASCENDING
        })
    }

    private fun generateViewModel(): ArtistsListViewModel {
        return ArtistsListViewModel(
            initialState = ArtistsListState(
                artistsList = listOf(),
                sortOrder = MediaSortOrder.DESCENDING,
                events = listOf()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onArtistClicked adds NavigateToArtist event`() = runTest {
        with(generateViewModel()) {
            onArtistClicked(ARTIST_NAME_0)
            assertEquals(ArtistsListUiEvent.NavigateToArtist(ARTIST_NAME_0), state.value.events)
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onUpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(ArtistsListUiEvent.NavigateUp, state.value.events)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ContextMenuIconClicked adds OpenContextMenu event`() = runTest {
        with(generateViewModel()) {
            onArtistOverflowMenuIconClicked(artistName = ARTIST_NAME_0)
            assertEquals(
                ArtistsListUiEvent.OpenContextMenu(artistName = ARTIST_NAME_0),
                state.value.events
            )
        }
    }

    companion object {
        private const val ARTIST_NAME_0 = "ARTIST_NAME_0"
        private const val ARTIST_NAME_1 = "ARTIST_NAME_1"
    }
}
