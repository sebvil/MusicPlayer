package com.sebastianvm.musicplayer.ui.library.tracks

import com.sebastianvm.musicplayer.database.entities.fullTrackInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertContains

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TracksListViewModelTest {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var playbackManager: PlaybackManager
    private lateinit var preferencesRepository: SortPreferencesRepository
    private lateinit var trackRepository: TrackRepository
    private lateinit var mediaQueueRepository: MediaQueueRepository

    @Before
    fun setUp() {
        playbackManager = spyk(FakePlaybackManager())
        preferencesRepository = FakeSortPreferencesRepository()
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
                    playlistIds {
                        add(TRACK_PLAYLIST_1)
                    }
                }),
        )
        mediaQueueRepository = FakeMediaQueueRepository()
    }

    private fun generateViewModel(
        listGroupType: TracksListType = TracksListType.ALL_TRACKS,
        tracksListTitle: String = TracksListViewModel.ALL_TRACKS,
    ): TracksListViewModel {
        return TracksListViewModel(
            mediaPlaybackRepository = playbackManager,
            initialState = TracksListState(
                tracksListName = tracksListTitle,
                tracksListType = listGroupType,
                tracksList = listOf(),
                currentSort = MediaSortOption.ARTIST,
                sortOrder = MediaSortOrder.DESCENDING,
            ),
            preferencesRepository = preferencesRepository,
            trackRepository = trackRepository,
            mediaQueueRepository = mediaQueueRepository
        )
    }

    @Test
    fun `init for all tracks sets initial state`() = runTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            with(state.value) {
                assertEquals(TracksListViewModel.ALL_TRACKS, tracksListName)
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

    @Test
    fun `init for genre sets initial state`() = runTest {
        with(
            generateViewModel(
                listGroupType = TracksListType.GENRE,
                tracksListTitle = TRACK_GENRE_0
            )
        ) {
            advanceUntilIdle()
            with(state.value) {
                assertEquals(TRACK_GENRE_0, tracksListName)
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

    @Test
    fun `init for playlist sets initial state`() = runTest {
        with(generateViewModel(listGroupType = TracksListType.PLAYLIST, tracksListTitle = TRACK_PLAYLIST_1)) {
            advanceUntilIdle()
            with(state.value) {
                assertEquals(TRACK_PLAYLIST_1, tracksListName)
                assertEquals(
                    listOf(
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

    @Test
    fun `onTrackClicked for all tracks triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel()) {
            onTrackClicked(TRACK_ID_0)
            advanceUntilIdle()
            assertEquals(listOf(TracksListUiEvent.NavigateToPlayer), events.value)
            verify {
                playbackManager.playFromId(
                    TRACK_ID_0,
                    MediaGroup(mediaGroupType = MediaGroupType.ALL_TRACKS, mediaId = "")
                )
            }
        }
    }

    @Test
    fun `onTrackClicked for genre triggers playback, adds nav to player event`() = runTest{
        with(
            generateViewModel(
                listGroupType = TracksListType.GENRE,
                tracksListTitle = TRACK_GENRE_0
            )
        ) {
            onTrackClicked(TRACK_ID_0)
            advanceUntilIdle()
            assertEquals(listOf(TracksListUiEvent.NavigateToPlayer), events.value)
            verify {
                playbackManager.playFromId(
                    TRACK_ID_0,
                    MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = TRACK_GENRE_0
                    )
                )
            }
        }
    }

    @Test
    fun `onTrackClicked for playlist triggers playback, adds nav to player event`() = runTest {
        with(
            generateViewModel(
                listGroupType = TracksListType.PLAYLIST,
                tracksListTitle = TRACK_PLAYLIST_1
            )
        ) {
            onTrackClicked(TRACK_ID_1)
            advanceUntilIdle()
            assertEquals(listOf(TracksListUiEvent.NavigateToPlayer), events.value)
            verify {
                playbackManager.playFromId(
                    TRACK_ID_1,
                    MediaGroup(
                        mediaGroupType = MediaGroupType.PLAYLIST,
                        mediaId = TRACK_PLAYLIST_1
                    )
                )
            }
        }
    }


    @Test
    fun `onSortByClicked adds ShowSortBottomSheet UiEvent`() {
        with(generateViewModel()) {
            onSortByClicked()
            assertContains(events.value, TracksListUiEvent.ShowSortBottomSheet)
        }
    }

    @Test
    fun `onTrackOverflowMenuIconClicked for all tracks adds OpenContextMenu UiEvent`() {
        with(generateViewModel()) {
            onTrackOverflowMenuIconClicked(TRACK_ID_0)
            assertContains(
                events.value, TracksListUiEvent.OpenContextMenu(
                    trackId = TRACK_ID_0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.ALL_TRACKS,
                        mediaId = ""
                    )
                )
            )
        }
    }

    @Test
    fun `onTrackOverflowMenuIconClicked for genre adds OpenContextMenu UiEvent`() {
        with(
            generateViewModel(
                listGroupType = TracksListType.GENRE,
                tracksListTitle = TRACK_GENRE_0
            )
        ) {
            onTrackOverflowMenuIconClicked(TRACK_ID_0)
            assertContains(
                events.value, TracksListUiEvent.OpenContextMenu(
                    trackId = TRACK_ID_0,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.GENRE,
                        mediaId = TRACK_GENRE_0
                    )
                )
            )
        }
    }

    @Test
    fun `onTrackOverflowMenuIconClicked for playlist adds OpenContextMenu UiEvent`() {
        with(
            generateViewModel(
                listGroupType = TracksListType.PLAYLIST,
                tracksListTitle = TRACK_PLAYLIST_1
            )
        ) {
            onTrackOverflowMenuIconClicked(TRACK_ID_1)
            assertContains(
                events.value, TracksListUiEvent.OpenContextMenu(
                    trackId = TRACK_ID_1,
                    mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.PLAYLIST,
                        mediaId = TRACK_PLAYLIST_1
                    )
                )
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `modifying sortOption changes order`() = runTest {
        with(generateViewModel()) {
            advanceUntilIdle()
            preferencesRepository.modifyTrackListSortOptions(
                ,
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = TracksListViewModel.ALL_TRACKS
            )
            advanceUntilIdle()
            assertEquals(MediaSortOption.TRACK, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)


            preferencesRepository.modifyTrackListSortOptions(
                ,
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            advanceUntilIdle()
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)

            preferencesRepository.modifyTrackListSortOptions(
                ,
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            advanceUntilIdle()
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)

            preferencesRepository.modifyTrackListSortOptions(
                ,
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            advanceUntilIdle()
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)


            preferencesRepository.modifyTrackListSortOptions(
                ,
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            advanceUntilIdle()
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
        }
    }
  
    @Test
    fun `onUpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertContains(events.value, TracksListUiEvent.NavigateUp)
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
        private const val TRACK_PLAYLIST_1 = "TRACK_PLAYLIST_1"
    }
}
