package com.sebastianvm.musicplayer.ui.library.tracks

import com.sebastianvm.musicplayer.database.entities.fullTrackInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.FakeMediaQueueRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TracksListViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var trackRepository: TrackRepository
    private lateinit var mediaQueueRepository: MediaQueueRepository

    @Before
    fun setUp() {
        mediaPlaybackRepository = spyk(FakeMediaPlaybackRepository())
        preferencesRepository = FakePreferencesRepository()
        trackRepository = FakeTrackRepository(
            tracks = listOf(fullTrackInfo {
                track {
                    trackId = TRACK_ID_0
                    trackName = TRACK_NAME_0
                    artists = TRACK_ARTIST_0
                    albumName = TRACK_ALBUM_0
                }
                genreIds {
                    add(TRACK_GENRE_0)
                }
            },
                fullTrackInfo {
                    track {
                        trackId = TRACK_ID_1
                        trackName = TRACK_NAME_1
                        artists = TRACK_ARTIST_1
                        albumName = TRACK_ALBUM_1
                    }
                }),
        )
        mediaQueueRepository = FakeMediaQueueRepository()
    }

    private fun generateViewModel(
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
                sortOrder = MediaSortOrder.DESCENDING,
                events = listOf()
            ),
            preferencesRepository = preferencesRepository,
            trackRepository = trackRepository,
            mediaQueueRepository = mediaQueueRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init for all tracks sets initial state`() = runTest {
        with(generateViewModel()) {
            with(state.value) {
                assertEquals("", tracksListTitle)
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
        with(generateViewModel(listGroupType = TracksListType.GENRE, genreName = TRACK_GENRE_0)) {
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
    fun `onTrackClicked for all tracks triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel()) {
            onTrackClicked(TRACK_ID_0)
            assertEquals(TracksListUiEvent.NavigateToPlayer, state.value.events)
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
    fun `onTrackClicked for genre triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel(listGroupType = TracksListType.GENRE, genreName = TRACK_GENRE_0)) {
            onTrackClicked(TRACK_ID_0)
            assertEquals(TracksListUiEvent.NavigateToPlayer, state.value.events)
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
    fun `onTrackClicked for genre sorted by artists triggers playback, adds nav to player event`() =
        runTest {
            with(
                generateViewModel(
                    listGroupType = TracksListType.GENRE,
                    genreName = TRACK_GENRE_0,
                )
            ) {
                onTrackClicked(TRACK_ID_0)
                assertEquals(TracksListUiEvent.NavigateToPlayer, state.value.events)
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
    fun `SortByClicked adds ShowSortBottomSheet UiEvent`() = runTest {
        with(generateViewModel()) {
            onSortByClicked()
            assertEquals(TracksListUiEvent.ShowSortBottomSheet, state.value.events)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onTrackOverflowMenuIconClicked for all tracks adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel()) {
            onTrackOverflowMenuIconClicked(TRACK_ID_0)
            assertEquals(
                TracksListUiEvent.OpenContextMenu(
                    trackId = TRACK_ID_0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.ALL_TRACKS,
                        mediaId = ""
                    )
                ), state.value.events
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onTrackOverflowMenuIconClicked  for genre adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel(listGroupType = TracksListType.GENRE, genreName = TRACK_GENRE_0)) {
            onTrackOverflowMenuIconClicked(TRACK_ID_0)
            assertEquals(
                TracksListUiEvent.OpenContextMenu(
                    trackId = TRACK_ID_0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = TRACK_GENRE_0
                    )
                ),
                state.value.events
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onUpButtonClicked adds NavigateUp event`() = runTest {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertEquals(TracksListUiEvent.NavigateUp, state.value.events)
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
