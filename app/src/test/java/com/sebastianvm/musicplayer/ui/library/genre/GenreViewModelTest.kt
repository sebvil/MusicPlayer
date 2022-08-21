package com.sebastianvm.musicplayer.ui.library.genre

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GenreViewModelTest : BaseTest() {

    private lateinit var genreRepository: GenreRepository

    @Before
    fun setUp() {
        genreRepository = mockk {
            every { getGenreName(C.ID_ONE) } returns flowOf(C.GENRE_ALPHA)
        }
    }

    private fun generateViewModel(): GenreViewModel {
        return GenreViewModel(
            initialState = GenreState(genreId = C.ID_ONE, genreName = ""),
            genreRepository = genreRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() =
        testScope.runReliableTest {
            with(generateViewModel()) {
                advanceUntilIdle()
                Assert.assertEquals(C.GENRE_ALPHA, state.value.genreName)
            }
        }


    @Test
    fun `SortByClicked navigates to sort menu for genre`() {
        with(generateViewModel()) {
            handle(GenreUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(trackListType = TrackListType.GENRE),
                                mediaId = C.ID_ONE
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
            handle(GenreUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }
}