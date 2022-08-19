package com.sebastianvm.musicplayer.ui.album

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumViewModelTest : BaseTest() {

    private lateinit var playbackManager: PlaybackManager
    private lateinit var albumRepository: AlbumRepository


    @Before
    fun setUp() {
        playbackManager = mockk()
        albumRepository = mockk {
            every { getAlbumWithTracks(C.ID_ONE) } returns MutableStateFlow(Fixtures.albumWithTracks)
        }
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            initialState = AlbumState(
                albumId = C.ID_ONE,
                trackList = listOf(),
                albumName = "",
                imageUri = "",
            ),
            albumRepository = albumRepository,
            playbackManager = playbackManager
        )
    }

    @Test
    fun `init sets initial state`() =
        testScope.runReliableTest {
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(
                    Fixtures.albumWithTracks.tracks.map { it.toModelListItemState() },
                    state.value.trackList
                )
                assertEquals(C.ALBUM_ALPACA, state.value.albumName)
            }
        }


    @Test
    fun `TrackClicked triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playAlbum(
                    initialTrackIndex = 0,
                    albumId = C.ID_ONE
                )
            } returns result
            with(generateViewModel()) {
                handle(AlbumUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(PlaybackResult.Error(errorMessage = 0), state.value.playbackResult)
            }
        }

    @Test
    fun `TrackClicked for triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playAlbum(
                    initialTrackIndex = 0,
                    albumId = C.ID_ONE
                )
            } returns result
            with(generateViewModel()) {
                handle(AlbumUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Success
                advanceUntilIdle()
                assertNull(state.value.playbackResult)
                assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }

    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu `() {
        with(generateViewModel()) {
            handle(AlbumUserAction.TrackOverflowMenuIconClicked(trackIndex = 1, trackId = 0))
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = C.ID_ONE,
                                    mediaGroupType = MediaGroupType.ALBUM
                                ),
                                trackIndex = 1
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }
}
