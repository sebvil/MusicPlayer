package com.sebastianvm.musicplayer.ui.components.lists.tracklist

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TrackListViewModelTest : BaseTest() {

    private lateinit var playbackManager: PlaybackManager
    private lateinit var trackRepository: TrackRepository
    private lateinit var playlistRepository: PlaylistRepository

    private val tracks = listOf(
        Fixtures.trackArgentina,
        Fixtures.trackBelgium,
        Fixtures.trackColombia,
    )

    private val tracksInPlaylist = listOf(
        Fixtures.trackWithPlaylistPositionArgentina,
        Fixtures.trackWithPlaylistPositionBelgium,
        Fixtures.trackWithPlaylistPositionColombia,
    )
    private val modelListItemStatesAscending = tracks.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()

    private val modelListItemStatesWithPositionAscending =
        tracksInPlaylist.map { it.toModelListItemState() }
    private val modelListItemStatesWithPositionDescending =
        modelListItemStatesWithPositionAscending.reversed()

    @Before
    fun setUp() {
        playbackManager = mockk()
        trackRepository = mockk {
            every { getAllTracks() } returns emptyFlow()
            every { getTracksForGenre(any()) } returns emptyFlow()
            every { getTracksForAlbum(any()) } returns emptyFlow()
        }
        playlistRepository = mockk {
            every { getTracksInPlaylist(any()) } returns emptyFlow()
        }
    }

    private fun generateViewModel(
        trackListType: TrackListType = TrackListType.ALL_TRACKS,
        trackListId: Long = 0,
        playbackResult: PlaybackResult? = null
    ): TrackListViewModel {
        return TrackListViewModel(
            initialState = TrackListState(
                trackListId = trackListId,
                trackListType = trackListType,
                trackList = listOf(),
                playbackResult = playbackResult
            ),
            trackRepository = trackRepository,
            playlistRepository = playlistRepository,
            playbackManager = playbackManager,
        )
    }

    private fun generateViewModelForGenre(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.GENRE, trackListId = C.ID_ONE)

    private fun generateViewModelForAlbum(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.ALBUM, trackListId = C.ID_ONE)

    private fun generateViewModelForPlaylist(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.PLAYLIST, trackListId = C.ID_ONE)


    // BEGIN SECTION ALL TRACKS

    @Test
    fun `init sets initial state and updates state on change to all tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracks)
            every { trackRepository.getAllTracks() } returns tracksFlow
            with(generateViewModel()) {
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesAscending, state.value.trackList)
                tracksFlow.value = tracks.reversed()
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }

    @Test
    fun `TrackClicked for all tracks triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playAllTracks(initialTrackIndex = 0) } returns result
            with(generateViewModel()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                Assert.assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for all tracks triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playAllTracks(initialTrackIndex = 0) } returns result
            with(generateViewModel()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Success
                advanceUntilIdle()
                assertNull(state.value.playbackResult)
                Assert.assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }

    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for all tracks`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.TrackOverflowMenuIconClicked(trackIndex = 0, trackId = 0))
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = 0,
                                    mediaGroupType = MediaGroupType.ALL_TRACKS
                                )
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    // END SECTION ALL TRACKS

    // BEGIN SECTION GENRE

    @Test
    fun `init sets initial state and updates state on change to genre tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracks)
            every { trackRepository.getTracksForGenre(C.ID_ONE) } returns tracksFlow
            with(generateViewModelForGenre()) {
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesAscending, state.value.trackList)
                tracksFlow.value = tracks.reversed()
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }


    @Test
    fun `TrackClicked for genre triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playGenre(genreId = C.ID_ONE) } returns result
            with(generateViewModelForGenre()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                Assert.assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for genre triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playGenre(genreId = C.ID_ONE) } returns result
            with(generateViewModelForGenre()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Success
                advanceUntilIdle()
                assertNull(state.value.playbackResult)
                Assert.assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for genre`() {
        with(generateViewModelForGenre()) {
            handle(TrackListUserAction.TrackOverflowMenuIconClicked(trackIndex = 0, trackId = 0))
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = C.ID_ONE,
                                    mediaGroupType = MediaGroupType.GENRE
                                )
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    // END SECTION GENRE


    // BEGIN SECTION PLAYLIST

    @Test
    fun `init sets initial state and updates state on change to playlist tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracksInPlaylist)
            every { playlistRepository.getTracksInPlaylist(C.ID_ONE) } returns tracksFlow
            with(generateViewModelForPlaylist()) {
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesWithPositionAscending, state.value.trackList)
                tracksFlow.value = tracksInPlaylist.reversed()
                advanceUntilIdle()
                Assert.assertEquals(
                    modelListItemStatesWithPositionDescending,
                    state.value.trackList
                )
            }
        }


    @Test
    fun `TrackClicked for playlist triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playPlaylist(playlistId = C.ID_ONE) } returns result
            with(generateViewModelForPlaylist()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                Assert.assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for playlist triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playPlaylist(playlistId = C.ID_ONE) } returns result
            with(generateViewModelForPlaylist()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Success
                advanceUntilIdle()
                assertNull(state.value.playbackResult)
                Assert.assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for playlist`() {
        with(generateViewModelForPlaylist()) {
            handle(TrackListUserAction.TrackOverflowMenuIconClicked(trackIndex = 0, trackId = 0))
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = C.ID_ONE,
                                    mediaGroupType = MediaGroupType.PLAYLIST
                                )
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    // END SECTION PLAYLIST

    // BEGIN SECTION ALBUM

    @Test
    fun `init sets initial state and updates state on change to album tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracks)
            every { trackRepository.getTracksForAlbum(C.ID_ONE) } returns tracksFlow
            with(generateViewModelForAlbum()) {
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesAscending, state.value.trackList)
                tracksFlow.value = tracks.reversed()
                advanceUntilIdle()
                Assert.assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }


    @Test
    fun `TrackClicked for album triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playAlbum(albumId = C.ID_ONE) } returns result
            with(generateViewModelForAlbum()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                Assert.assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for album triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playAlbum(albumId = C.ID_ONE) } returns result
            with(generateViewModelForAlbum()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                Assert.assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Success
                advanceUntilIdle()
                assertNull(state.value.playbackResult)
                Assert.assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for album`() {
        with(generateViewModelForAlbum()) {
            handle(TrackListUserAction.TrackOverflowMenuIconClicked(trackIndex = 0, trackId = 0))
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = C.ID_ONE,
                                    mediaGroupType = MediaGroupType.ALBUM
                                )
                            )
                        )
                    )
                ),
                navEvents.value
            )
        }
    }

    // END SECTION GENRE
    @Test
    fun `DismissPlaybackErrorDialog resets playback status state`() {
        with(generateViewModel(playbackResult = PlaybackResult.Error(0))) {
            handle(TrackListUserAction.DismissPlaybackErrorDialog)
            assertNull(state.value.playbackResult)
        }
    }
}
