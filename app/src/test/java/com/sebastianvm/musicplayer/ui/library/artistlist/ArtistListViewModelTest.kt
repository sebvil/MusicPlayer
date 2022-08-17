package com.sebastianvm.musicplayer.ui.library.artistlist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistListViewModelTest : BaseTest() {

    private lateinit var artistRepository: ArtistRepository
    private lateinit var preferencesRepository: SortPreferencesRepository

    private val artists = listOf(
        Fixtures.artistAna,
        Fixtures.artistBob,
        Fixtures.artistCamilo,
    )
    private val modelListItemStatesAscending = artists.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()

    @Before
    fun setUp() {
        artistRepository = mockk {
            every { getArtists() } returns emptyFlow()
        }

        preferencesRepository = mockk(relaxUnitFun = true)

    }

    private fun generateViewModel(): ArtistListViewModel {
        return ArtistListViewModel(
            initialState = ArtistListState(artistList = listOf()),
            artistRepository = artistRepository,
            preferencesRepository = preferencesRepository,
        )
    }

    @Test
    fun `init sets initial state and updates state on change to artist list`() =
        testScope.runReliableTest {
            val artistsFlow = MutableStateFlow(artists)
            every { artistRepository.getArtists() } returns artistsFlow
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.artistList)
                artistsFlow.value = artists.reversed()
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.artistList)
            }
        }

    @Test
    fun `onArtistClicked adds NavigateToArtist event`() {
        with(generateViewModel()) {
            handle(ArtistListUserAction.ArtistRowClicked(C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Artist(
                            ArtistArguments(artistId = C.ID_ONE)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `onUpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(ArtistListUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

    @Test
    fun `SortByClicked toggles artist list sort order`() = testScope.runReliableTest {
        with(generateViewModel()) {
            handle(ArtistListUserAction.SortByButtonClicked)
            advanceUntilIdle()
            coVerify { preferencesRepository.toggleArtistListSortOrder() }
        }
    }

    @Test
    fun `ContextMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            handle(ArtistListUserAction.ArtistOverflowMenuIconClicked(artistId = C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.ArtistContextMenu(
                            ArtistContextMenuArguments(artistId = C.ID_ONE)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }
}
