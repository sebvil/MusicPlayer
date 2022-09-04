package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class TrackListViewModelTest : BaseTest() {

    private lateinit var playbackManager: PlaybackManager
    private lateinit var trackRepository: TrackRepository

    private val tracks = listOf(
        Fixtures.trackArgentina,
        Fixtures.trackBelgium,
        Fixtures.trackColombia,
    )

    private val modelListItemStatesAscending = tracks.map { it.toModelListItemState() }
    private val modelListItemStatesDescending = modelListItemStatesAscending.reversed()

    @Before
    fun setUp() {
        playbackManager = mockk()
        trackRepository = mockk {
            every { getTracksForMedia(any(), any()) } returns emptyFlow()
            every { getTrackListMetadata(any(), any()) } returns emptyFlow()
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
            playbackManager = playbackManager,
        )
    }

    private fun generateViewModelForGenre(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.GENRE, trackListId = C.ID_ONE)

    private fun generateViewModelForAlbum(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.ALBUM, trackListId = C.ID_ONE)

    private fun generateViewModelForPlaylist(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.PLAYLIST, trackListId = C.ID_ONE)


    @Test
    fun `init sets initial state and updates state on change to all tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(modelListItemStatesAscending)
            every {
                trackRepository.getTracksForMedia(
                    trackListType = TrackListType.ALL_TRACKS,
                    mediaId = 0
                )
            } returns tracksFlow
            every {
                trackRepository.getTrackListMetadata(
                    mediaId = 0,
                    trackListType = TrackListType.ALL_TRACKS
                )
            } returns flowOf(
                TrackListMetadata()
            )

            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                assertNull(state.value.trackListName)
                assertNull(state.value.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }

    @Test
    fun `TrackClicked for all tracks triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(mediaGroupType = MediaGroupType.ALL_TRACKS, mediaId = 0)
                )
            } returns result
            with(generateViewModel()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for all tracks triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(mediaGroupType = MediaGroupType.ALL_TRACKS, mediaId = 0)
                )
            } returns result
            with(generateViewModel()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
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
    fun `TrackOverflowMenuIconClicked navigates to track context menu`() {
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
            val tracksFlow = MutableStateFlow(modelListItemStatesAscending)
            every {
                trackRepository.getTracksForMedia(
                    trackListType = TrackListType.GENRE,
                    mediaId = C.ID_ONE
                )
            } returns tracksFlow
            every {
                trackRepository.getTrackListMetadata(
                    mediaId = C.ID_ONE,
                    trackListType = TrackListType.GENRE
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.GENRE_ALPHA,
                )
            )
            with(generateViewModelForGenre()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                assertEquals(C.GENRE_ALPHA, state.value.trackListName)
                assertNull(state.value.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }


    @Test
    fun `TrackClicked for genre triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForGenre()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for genre triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForGenre()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
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
            val tracksFlow = MutableStateFlow(modelListItemStatesAscending)
            every {
                trackRepository.getTracksForMedia(
                    trackListType = TrackListType.PLAYLIST,
                    mediaId = C.ID_ONE
                )
            } returns tracksFlow

            every {
                trackRepository.getTrackListMetadata(
                    mediaId = C.ID_ONE,
                    trackListType = TrackListType.PLAYLIST
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.PLAYLIST_APPLE,
                )
            )

            with(generateViewModelForPlaylist()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                assertEquals(C.PLAYLIST_APPLE, state.value.trackListName)
                assertNull(state.value.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }


    @Test
    fun `TrackClicked for playlist triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.PLAYLIST,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForPlaylist()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for playlist triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.PLAYLIST,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForPlaylist()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
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
            val tracksFlow = MutableStateFlow(modelListItemStatesAscending)
            every {
                trackRepository.getTracksForMedia(
                    trackListType = TrackListType.ALBUM,
                    mediaId = C.ID_ONE
                )
            } returns tracksFlow

            val mediaArtState = MediaArtImageState(
                imageUri = C.IMAGE_URI_1,
                contentDescription = R.string.album_art_for_album,
                backupResource = R.drawable.ic_album,
                backupContentDescription = R.string.placeholder_album_art,
                args = listOf(C.ALBUM_ALPACA)
            )
            every {
                trackRepository.getTrackListMetadata(
                    mediaId = C.ID_ONE,
                    trackListType = TrackListType.ALBUM
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.ALBUM_ALPACA,
                    mediaArtImageState = mediaArtState
                )
            )
            with(generateViewModelForAlbum()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                assertEquals(C.ALBUM_ALPACA, state.value.trackListName)
                assertEquals(mediaArtState, state.value.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
            }
        }


    @Test
    fun `TrackClicked for album triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForAlbum()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.value.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for album triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every {
                playbackManager.playMedia(
                    initialTrackIndex = 0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = C.ID_ONE
                    )
                )
            } returns result
            with(generateViewModelForAlbum()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
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

    // END SECTION ALBUM
    @Test
    fun `DismissPlaybackErrorDialog resets playback status state`() {
        with(generateViewModel(playbackResult = PlaybackResult.Error(0))) {
            handle(TrackListUserAction.DismissPlaybackErrorDialog)
            assertNull(state.value.playbackResult)
        }
    }
}