package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.database.entities.TrackListMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playback.mediatree.MediaTree
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.playlist.TrackSearchArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.BaseTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
            every { getTracksForMedia(any()) } returns emptyFlow()
            every { getTrackListMetadata(any()) } returns emptyFlow()
        }

    }

    private fun generateViewModel(
        trackList: TrackList = MediaTree.KeyType.ALL_TRACKS,
        trackListId: Long = 0,
        playbackResult: PlaybackResult? = null
    ): TrackListViewModel {
        return TrackListViewModel(
            initialState = TrackListState(
                trackListId = trackListId,
                trackListType = trackList,
                trackList = listOf(),
                playbackResult = playbackResult
            ),
            trackRepository = trackRepository,
            playbackManager = playbackManager,
        )
    }

    private fun generateViewModelForGenre(): TrackListViewModel =
        generateViewModel(trackList = TrackList.GENRE, trackListId = C.ID_ONE)

    private fun generateViewModelForAlbum(): TrackListViewModel =
        generateViewModel(trackList = TrackList.ALBUM, trackListId = C.ID_ONE)

    private fun generateViewModelForPlaylist(): TrackListViewModel =
        generateViewModel(trackList = TrackList.PLAYLIST, trackListId = C.ID_ONE)


    @Test
    fun `init sets initial state and updates state on change to all tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(modelListItemStatesAscending)
            every {
                trackRepository.getTracksForMedia(
                    trackList = MediaTree.KeyType.ALL_TRACKS
                )
            } returns tracksFlow
            every {
                trackRepository.getTrackListMetadata(
                    trackList = MediaTree.KeyType.ALL_TRACKS
                )
            } returns flowOf(
                TrackListMetadata()
            )

            with(generateViewModel()) {
                assertEquals(modelListItemStatesAscending, state.trackList)
                assertNull(state.trackListName)
                assertNull(state.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                assertEquals(modelListItemStatesDescending, state.trackList)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.playbackResult
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Success
                assertNull(state.playbackResult)
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
                                trackList = MediaGroup(
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

    @Test
    fun `SortByButtonClicked navigates to sort menu for all tracks`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(trackList = TrackList.ALL_TRACKS),
                                mediaId = 0
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
                    trackList = TrackList.GENRE
                )
            } returns tracksFlow
            every {
                trackRepository.getTrackListMetadata(
                    trackList = TrackList.GENRE
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.GENRE_ALPHA,
                )
            )
            with(generateViewModelForGenre()) {
                assertEquals(modelListItemStatesAscending, state.trackList)
                assertEquals(C.GENRE_ALPHA, state.trackListName)
                assertNull(state.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                assertEquals(modelListItemStatesDescending, state.trackList)
            }
        }


    @Test
    fun `TrackClicked for genre triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for genre triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Success
                assertNull(state.playbackResult)
                assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for genre`() {
        with(generateViewModelForGenre()) {
            handle(
                TrackListUserAction.TrackOverflowMenuIconClicked(
                    trackIndex = 0,
                    trackId = 0
                )
            )
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                trackList = MediaGroup(
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

    @Test
    fun `SortByButtonClicked navigates to sort menu for genre`() {
        with(generateViewModelForGenre()) {
            handle(TrackListUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Tracks(trackList = TrackList.GENRE),
                                mediaId = 1
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
                    trackList = TrackList.PLAYLIST
                )
            } returns tracksFlow

            every {
                trackRepository.getTrackListMetadata(
                    trackList = TrackList.PLAYLIST
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.PLAYLIST_APPLE,
                )
            )

            with(generateViewModelForPlaylist()) {
                assertEquals(modelListItemStatesAscending, state.trackList)
                assertEquals(C.PLAYLIST_APPLE, state.trackListName)
                assertNull(state.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                assertEquals(modelListItemStatesDescending, state.trackList)
            }
        }


    @Test
    fun `TrackClicked for playlist triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for playlist triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Success
                assertNull(state.playbackResult)
                assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for playlist`() {
        with(generateViewModelForPlaylist()) {
            handle(
                TrackListUserAction.TrackOverflowMenuIconClicked(
                    trackIndex = 0,
                    trackId = 0
                )
            )
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                trackList = MediaGroup(
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

    @Test
    fun `SortByButtonClicked navigates to sort menu for playlist`() {
        with(generateViewModelForPlaylist()) {
            handle(TrackListUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Playlist,
                                mediaId = 1
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
                    trackList = TrackList.ALBUM
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
                    trackList = TrackList.ALBUM
                )
            } returns flowOf(
                TrackListMetadata(
                    trackListName = C.ALBUM_ALPACA,
                    mediaArtImageState = mediaArtState
                )
            )
            with(generateViewModelForAlbum()) {
                assertEquals(modelListItemStatesAscending, state.trackList)
                assertEquals(C.ALBUM_ALPACA, state.trackListName)
                assertEquals(mediaArtState, state.headerImage)
                tracksFlow.value = modelListItemStatesDescending
                assertEquals(modelListItemStatesDescending, state.trackList)
            }
        }


    @Test
    fun `TrackClicked for album triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                assertEquals(
                    PlaybackResult.Error(errorMessage = 0),
                    state.playbackResult
                )
            }
        }

    @Test
    fun `TrackClicked for album triggers playback and on success navigates to player`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> =
                MutableStateFlow(PlaybackResult.Loading)
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
                assertEquals(PlaybackResult.Loading, state.playbackResult)
                result.value = PlaybackResult.Success
                assertNull(state.playbackResult)
                assertEquals(
                    listOf(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer)),
                    navEvents.value
                )
            }
        }


    @Test
    fun `TrackOverflowMenuIconClicked navigates to track context menu for album`() {
        with(generateViewModelForAlbum()) {
            handle(
                TrackListUserAction.TrackOverflowMenuIconClicked(
                    trackIndex = 0,
                    trackId = 0
                )
            )
            kotlin.test.assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = 0,
                                mediaType = MediaType.TRACK,
                                trackList = MediaGroup(
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

    @Test
    fun `SortByButtonClicked throws error for album`() {
        with(generateViewModelForAlbum()) {
            assertThrows(IllegalStateException::class.java) {
                handle(TrackListUserAction.SortByButtonClicked)
            }
        }
    }


    // END SECTION ALBUM
    @Test
    fun `DismissPlaybackErrorDialog resets playback status state`() {
        with(generateViewModel(playbackResult = PlaybackResult.Error(0))) {
            handle(TrackListUserAction.DismissPlaybackErrorDialog)
            assertNull(state.playbackResult)
        }
    }

    @Test
    fun `UpButtonClicked navigates up`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

    @Test
    fun `AddTracksClicked navigates to TrackSearch screen`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.AddTracksClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackSearch(
                            TrackSearchArguments(0)
                        )
                    )
                ),
                navEvents.value
            )
        }
    }
}
