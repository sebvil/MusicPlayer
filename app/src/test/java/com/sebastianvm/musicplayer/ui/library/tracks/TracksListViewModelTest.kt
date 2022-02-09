package com.sebastianvm.musicplayer.ui.library.tracks

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.FakeMediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TracksListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository

    @Before
    fun setUp() {
        mediaPlaybackRepository = spyk(FakeMediaPlaybackRepository())
    }

    private fun generateViewModel(
        preferencesRepository: FakePreferencesRepository = FakePreferencesRepository(),
        listGroupType: TracksListType = TracksListType.ALL_TRACKS,
        genreName: String = "",
    ): TracksListViewModel {
        return TracksListViewModel(
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = TracksListState(
                tracksListTitle = genreName,
                tracksListType = listGroupType,
                tracksList = listOf(),
                currentSort = MediaSortOption.ARTIST,
                sortOrder = MediaSortOrder.DESCENDING
            ),
            preferencesRepository = preferencesRepository,
            trackRepository = FakeTrackRepository(),
            mediaQueueRepository = FakeMediaQueueRepository()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init for all tracks sets initial state`() = runTest {
        with(generateViewModel()) {
            delay(1)
            with(state.value) {
                assertNull(tracksListTitle)
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TRACK_ID_0,
                            trackName = TRACK_NAME_0,
                            artists = TRACK_ARTIST_0,
                            albumName = TRACK_ALBUM_0,
                            trackNumber = null
                        ),
                        TrackRowState(
                            trackId = TRACK_ID_1,
                            trackName = TRACK_NAME_1,
                            artists = TRACK_ARTIST_1,
                            albumName = TRACK_ALBUM_1,
                            trackNumber = null
                        ),
                    ), tracksList
                )
                assertEquals(MediaSortOption.TRACK, currentSort)
                assertEquals(MediaSortOrder.ASCENDING, sortOrder)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init for genre sets initial state`() = runTest {
        with(generateViewModel(genreName = TRACK_GENRE_0)) {
            delay(1)
            with(state.value) {
                assertEquals(TRACK_GENRE_0, tracksListTitle)
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TRACK_ID_0,
                            trackName = TRACK_NAME_0,
                            artists = TRACK_ARTIST_0,
                            albumName = TRACK_ALBUM_0,
                            trackNumber = null
                        ),
                    ), tracksList
                )
                assertEquals(MediaSortOption.TRACK, currentSort)
                assertEquals(MediaSortOrder.ASCENDING, sortOrder)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for all tracks triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TRACK_ID_0))
            delay(1)
            verify {
                mediaPlaybackRepository.playFromId(
                    TRACK_ID_0,
                    MediaGroup(mediaGroupType = MediaGroupType.ALL_TRACKS, mediaId = "")
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for genre triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel(genreName = TRACK_GENRE_0)) {
            expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TRACK_ID_0))
            delay(1)
            verify {
                mediaPlaybackRepository.playFromId(
                    TRACK_ID_0,
                    MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = TRACK_GENRE_0
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for genre sorted by artists triggers playback, adds nav to player event`() =
        runTest {
            with(
                generateViewModel(
                    preferencesRepository = FakePreferencesRepository(),
                    genreName = TRACK_GENRE_0,
                )
            ) {
                expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
                handle(TracksListUserAction.TrackClicked(TRACK_ID_0))
                delay(1)
                verify {
                    mediaPlaybackRepository.playFromId(
                        TRACK_ID_0,
                        MediaGroup(
                            mediaGroupType = MediaGroupType.GENRE,
                            mediaId = TRACK_GENRE_0
                        )
                    )
                }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `SortByClicked changes state`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.ShowSortBottomSheet>(this@runTest)
            handle(TracksListUserAction.SortByClicked)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `MediaSortOptionClicked changes state`() = runTest {
        val tracksList = listOf(
            TrackRowState(
                trackId = TRACK_ID_0,
                trackName = TRACK_NAME_0,
                artists = TRACK_ARTIST_0,
                albumName = TRACK_ALBUM_0,
                trackNumber = null
            ),
            TrackRowState(
                trackId = TRACK_ID_1,
                trackName = TRACK_NAME_1,
                artists = TRACK_ARTIST_1,
                albumName = TRACK_ALBUM_1,
                trackNumber = null
            )
        )
        with(generateViewModel()) {
            delay(1)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.ARTIST))
            delay(1)
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.ARTIST))
            delay(1)
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.TRACK))
            delay(1)
            assertEquals(MediaSortOption.TRACK, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.TRACK))
            delay(1)
            assertEquals(MediaSortOption.TRACK, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.ALBUM))
            delay(1)
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.MediaSortOptionClicked(MediaSortOption.ALBUM))
            delay(1)
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackContextMenuClicked  for all tracks adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(TRACK_ID_0, trackId)
                assertEquals(MediaGroup(MediaGroupType.ALL_TRACKS, "ALL_TRACKS"), mediaGroup)
            }
            handle(TracksListUserAction.TrackContextMenuClicked(TRACK_ID_0))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackContextMenuClicked  for genre adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel(genreName = TRACK_GENRE_0)) {
            expectUiEvent<TracksListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(TRACK_ID_0, trackId)
                assertEquals(MediaGroup(MediaGroupType.GENRE, TRACK_GENRE_0), mediaGroup)
            }
            handle(TracksListUserAction.TrackContextMenuClicked(TRACK_ID_0))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `UpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.NavigateUp>(this@runTest)
            handle(TracksListUserAction.UpButtonClicked)
        }
    }
    
    companion object {
        private const val TRACK_ID_0 = "0"
        private const val TRACK_NAME_0 = "TRACK_NAME_0"
        private const val TRACK_ALBUM_0 = "0"
        private const val TRACK_ARTIST_0 = "TRACK_ARTIST_0"
        private const val TRACK_GENRE_0 = "TRACK_GENRE_0"

        private const val TRACK_ID_1 = "1"
        private const val TRACK_NAME_1 = "TRACK_NAME_1"
        private const val TRACK_ALBUM_1 = "1"
        private const val TRACK_ARTIST_1 = "TRACK_ARTIST_1"

    }
}
