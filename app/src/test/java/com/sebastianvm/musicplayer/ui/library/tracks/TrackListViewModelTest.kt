package com.sebastianvm.musicplayer.ui.library.tracks

import com.sebastianvm.musicplayer.database.entities.C
import com.sebastianvm.musicplayer.database.entities.Fixtures
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
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
    private lateinit var genreRepository: GenreRepository

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
            every { getAllTracks() } returns emptyFlow()
            every { getTracksForGenre(any()) } returns emptyFlow()
        }
        genreRepository = mockk {
            every { getGenreName(C.ID_ONE) } returns flowOf(C.GENRE_ALPHA)
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
                trackListName = null,
                trackListType = trackListType,
                trackList = listOf(),
                playbackResult = playbackResult
            ),
            trackRepository = trackRepository,
            genreRepository = genreRepository,
            playbackManager = playbackManager
        )
    }

    private fun generateViewModelForGenre(): TrackListViewModel =
        generateViewModel(trackListType = TrackListType.GENRE, trackListId = C.ID_ONE)

    @Test
    fun `init sets initial state and updates state on change to all tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracks)
            every { trackRepository.getAllTracks() } returns tracksFlow
            with(generateViewModel()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                tracksFlow.value = tracks.reversed()
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
                assertNull(state.value.trackListName)
            }
        }

    @Test
    fun `init sets initial state and updates state on change to genre tracks list`() =
        testScope.runReliableTest {
            val tracksFlow = MutableStateFlow(tracks)
            every { trackRepository.getTracksForGenre(C.ID_ONE) } returns tracksFlow
            with(generateViewModelForGenre()) {
                advanceUntilIdle()
                assertEquals(modelListItemStatesAscending, state.value.trackList)
                tracksFlow.value = tracks.reversed()
                advanceUntilIdle()
                assertEquals(modelListItemStatesDescending, state.value.trackList)
                assertEquals(C.GENRE_ALPHA, state.value.trackListName)
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
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(PlaybackResult.Error(errorMessage = 0), state.value.playbackResult)
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
    fun `TrackClicked for genre triggers playback and on failure sets playback result`() =
        testScope.runReliableTest {
            val result: MutableStateFlow<PlaybackResult> = MutableStateFlow(PlaybackResult.Loading)
            every { playbackManager.playGenre(genreId = C.ID_ONE) } returns result
            with(generateViewModelForGenre()) {
                handle(TrackListUserAction.TrackClicked(trackIndex = 0))
                advanceUntilIdle()
                assertEquals(PlaybackResult.Loading, state.value.playbackResult)
                result.value = PlaybackResult.Error(errorMessage = 0)
                advanceUntilIdle()
                assertEquals(PlaybackResult.Error(errorMessage = 0), state.value.playbackResult)
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
    fun `SortByClicked navigates to sort menu for all tracks`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.SortByButtonClicked)
            assertEquals(
                listOf(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(listType = SortableListType.Tracks(trackListType = TrackListType.ALL_TRACKS))
                        )
                    )
                ), navEvents.value
            )
        }
    }

    @Test
    fun `SortByClicked navigates to sort menu for genre`() {
        with(generateViewModelForGenre()) {
            handle(TrackListUserAction.SortByButtonClicked)
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
                ), navEvents.value
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


    @Test
    fun `UpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            handle(TrackListUserAction.UpButtonClicked)
            assertEquals(listOf(NavEvent.NavigateUp), navEvents.value)
        }
    }

    @Test
    fun `DismissPlaybackErrorDialog resets playback status state`() {
        with(generateViewModel(playbackResult = PlaybackResult.Error(0))) {
            handle(TrackListUserAction.DismissPlaybackErrorDialog)
            assertNull(state.value.playbackResult)
        }
    }
}
