package com.sebastianvm.musicplayer.ui.library.playlistlist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistListViewModelTest : BaseTest() {

    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var sortPreferencesRepository: SortPreferencesRepository
    private val playlists = listOf(
        Fixtures.playlistApple,
        Fixtures.playlistBanana,
        Fixtures.playlistCarrot
    )
    private val modelListItemStatesAscending = playlists.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()


    @Before
    fun setUp() {
        playlistRepository = mockk {
            every { getPlaylists() } returns emptyFlow()
        }
        sortPreferencesRepository = mockk(relaxUnitFun = true)
    }

    private fun generateViewModel(
        isCreatePlaylistDialogOpen: Boolean = false,
        isPlaylistCreationErrorDialogOpen: Boolean = false
    ): PlaylistListViewModel {
        return PlaylistListViewModel(
            initialState = PlaylistListState(
                playlistList = listOf(),
                isCreatePlaylistDialogOpen = isCreatePlaylistDialogOpen,
                isPlaylistCreationErrorDialogOpen = isPlaylistCreationErrorDialogOpen
            ),
            playlistRepository = playlistRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init sets initial state and updates state on change to playlist list`() =
        testScope.runReliableTest {
            val playlistsFlow = MutableStateFlow(playlists)
            every { playlistRepository.getPlaylists() } returns playlistsFlow
            with(generateViewModel()) {
                Assert.assertEquals(modelListItemStatesAscending, state.playlistList)
                playlistsFlow.value = playlists.reversed()
                Assert.assertEquals(modelListItemStatesDescending, state.playlistList)
            }
        }

    @Test
    fun `PlaylistClicked adds NavigateToPlaylist event`() {
        with(generateViewModel()) {
            handle(PlaylistListUserAction.PlaylistClicked(C.ID_ONE))
            Assert.assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.TrackList(
                        TrackListArgumentsForNav(
                            trackListType = TrackList.PLAYLIST,
                            trackListId = C.ID_ONE
                        )
                    )
                )
            )
        }
    }

    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(PlaylistListUserAction.UpButtonClicked)
            Assert.assertEquals(navEvents.value.first(), NavEvent.NavigateUp)
        }
    }

    @Test
    fun `SortByButtonClicked toggles playlist sort order`() = testScope.runReliableTest {
        with(generateViewModel()) {
            handle(PlaylistListUserAction.SortByClicked)
            coVerify { sortPreferencesRepository.togglePlaylistListSortOder() }
        }
    }


    @Test
    fun `PlaylistOverflowMenuIconClicked adds OpenContextMenu event`() {
        with(generateViewModel()) {
            handle(PlaylistListUserAction.PlaylistOverflowMenuIconClicked(playlistId = C.ID_ONE))
            Assert.assertEquals(
                navEvents.value.first(),
                NavEvent.NavigateToScreen(
                    NavigationDestination.PlaylistContextMenu(
                        PlaylistContextMenuArguments(playlistId = C.ID_ONE)
                    )
                )
            )
        }
    }

    @Test
    fun `AddPlaylistButtonClicked opens playlist creation dialog`() {
        with(generateViewModel()) {
            handle(PlaylistListUserAction.AddPlaylistButtonClicked)
            assertTrue(state.isCreatePlaylistDialogOpen)
        }
    }

    @Test
    fun `DismissPlaylistCreationButtonClicked closes playlist creation dialog`() {
        with(generateViewModel(isCreatePlaylistDialogOpen = true)) {
            handle(PlaylistListUserAction.DismissPlaylistCreationButtonClicked)
            assertFalse(state.isCreatePlaylistDialogOpen)
        }
    }

    @Test
    fun `DismissPlaylistCreationErrorDialog closes playlist creation dialog`() {
        with(generateViewModel(isPlaylistCreationErrorDialogOpen = true)) {
            handle(PlaylistListUserAction.DismissPlaylistCreationErrorDialog)
            assertFalse(state.isPlaylistCreationErrorDialogOpen)
        }
    }

    @Test
    fun `CreatePlaylistButtonClicked closes playlist creation dialog and opens error dialog on error`() =
        testScope.runReliableTest {
            every { playlistRepository.createPlaylist(C.PLAYLIST_APPLE) } returns flowOf(null)
            with(generateViewModel(isCreatePlaylistDialogOpen = true)) {
                handle(PlaylistListUserAction.CreatePlaylistButtonClicked(playlistName = C.PLAYLIST_APPLE))
                assertFalse(state.isCreatePlaylistDialogOpen)
                assertTrue(state.isPlaylistCreationErrorDialogOpen)
            }
        }

    @Test
    fun `CreatePlaylistButtonClicked closes playlist creation dialog on success`() =
        testScope.runReliableTest {
            every { playlistRepository.createPlaylist(C.PLAYLIST_APPLE) } returns flowOf(1)
            with(generateViewModel(isCreatePlaylistDialogOpen = true)) {
                handle(PlaylistListUserAction.CreatePlaylistButtonClicked(playlistName = C.PLAYLIST_APPLE))
                assertFalse(state.isCreatePlaylistDialogOpen)
                assertFalse(state.isPlaylistCreationErrorDialogOpen)
            }
        }


}