package com.sebastianvm.musicplayer.ui.library.genrelist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
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
class GenreListViewModelTest : BaseTest() {

    private lateinit var genreRepository: GenreRepository
    private lateinit var preferencesRepository: SortPreferencesRepository
    private val genres: List<Genre> = listOf(
        Fixtures.genreAlpha,
        Fixtures.genreBravo,
        Fixtures.genreCharlie
    )
    private val modelListItemStatesAscending = genres.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()

    @Before
    fun setUp() {
        genreRepository = mockk {
            every { getGenres() } returns emptyFlow()
        }
        preferencesRepository = mockk(relaxUnitFun = true)

    }

    private fun generateViewModel(): GenreListViewModel {
        return GenreListViewModel(
            initialState = GenreListState(genreList = listOf()),
            genreRepository = genreRepository,
            preferencesRepository = preferencesRepository,
        )
    }

    @Test
    fun `init sets initial state and updates state on change to genres list`() =
        testScope.runReliableTest {
            val genresFlow = MutableStateFlow(genres)
            every { genreRepository.getGenres() } returns genresFlow
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.genreList)
                genresFlow.value = genres.reversed()
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.genreList)
            }
        }

    @Test
    fun `GenreClicked adds NavigateToGenre event`() {
        with(generateViewModel()) {
            handle(GenreListUserAction.GenreRowClicked(genreId = C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackList(
                            TrackListArguments(
                                trackListType = TrackListType.GENRE,
                                trackListId = C.ID_ONE
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(GenreListUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

    @Test
    fun `SortByClicked toggles artist list sort order`() = testScope.runReliableTest {
        with(generateViewModel()) {
            handle(GenreListUserAction.SortByButtonClicked)
            advanceUntilIdle()
            coVerify { preferencesRepository.toggleGenreListSortOrder() }
        }
    }

    @Test
    fun `OverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            handle(GenreListUserAction.GenreOverflowMenuIconClicked(genreId = C.ID_ONE))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.GenreContextMenu(
                            GenreContextMenuArguments(genreId = C.ID_ONE)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }
}
