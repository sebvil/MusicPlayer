package com.sebastianvm.musicplayer.ui.playlist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
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

class PlaylistViewModelTest : BaseTest() {

    private lateinit var playlistRepository: PlaylistRepository

    @Before
    fun setUp() {
        playlistRepository = mockk {
            every { getPlaylistName(C.ID_ONE) } returns flowOf(C.PLAYLIST_APPLE)
        }
    }

    private fun generateViewModel(): PlaylistViewModel {
        return PlaylistViewModel(
            initialState = PlaylistState(playlistId = C.ID_ONE, playlistName = ""),
            playlistRepository = playlistRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets initial state`() =
        testScope.runReliableTest {
            with(generateViewModel()) {
                advanceUntilIdle()
                Assert.assertEquals(C.PLAYLIST_APPLE, state.value.playlistName)
            }
        }


    @Test
    fun `SortByClicked navigates to sort menu for playlist`() {
        with(generateViewModel()) {
            handle(PlaylistUserAction.SortByClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Playlist,
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
            handle(PlaylistUserAction.UpClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }
}