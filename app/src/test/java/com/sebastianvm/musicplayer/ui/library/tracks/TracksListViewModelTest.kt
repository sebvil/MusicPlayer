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
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains

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
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = TracksListState(
                tracksListTitle = tracksListTitle,
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
                assertEquals(TracksListViewModel.ALL_TRACKS, tracksListTitle)
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
        with(
            generateViewModel(
                listGroupType = TracksListType.GENRE,
                tracksListTitle = TRACK_GENRE_0
            )
        ) {
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
    fun `init for playlist sets initial state`() = runTest {
        with(
            generateViewModel(
                listGroupType = TracksListType.PLAYLIST,
                tracksListTitle = TRACK_PLAYLIST_1
            )
        ) {
            with(state.value) {
                assertEquals(TRACK_PLAYLIST_1, tracksListTitle)
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
    fun `onTrackClicked for all tracks triggers playback, adds nav to player event`() {
        with(generateViewModel()) {
            onTrackClicked(TRACK_ID_0)
            assertContains(state.value.events, TracksListUiEvent.NavigateToPlayer)
            verify {
                mediaPlaybackRepository.playFromId(
                    TRACK_ID_0,
                    MediaGroup(mediaGroupType = MediaGroupType.ALL_TRACKS, mediaId = "")
                )
            }
        }
    }

    @Test
    fun `onTrackClicked for genre triggers playback, adds nav to player event`() {
        with(
            generateViewModel(
                listGroupType = TracksListType.GENRE,
                tracksListTitle = TRACK_GENRE_0
            )
        ) {
            onTrackClicked(TRACK_ID_0)
            assertContains(state.value.events, TracksListUiEvent.NavigateToPlayer)
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

    @Test
    fun `onTrackClicked for playlist triggers playback, adds nav to player event`() {
        with(
            generateViewModel(
                listGroupType = TracksListType.PLAYLIST,
                tracksListTitle = TRACK_PLAYLIST_1
            )
        ) {
            onTrackClicked(TRACK_ID_1)
            assertContains(state.value.events, TracksListUiEvent.NavigateToPlayer)
            verify {
                mediaPlaybackRepository.playFromId(
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
            assertContains(state.value.events, TracksListUiEvent.ShowSortBottomSheet)
        }
    }

    @Test
    fun `onTrackOverflowMenuIconClicked for all tracks adds OpenContextMenu UiEvent`() {
        with(generateViewModel()) {
            onTrackOverflowMenuIconClicked(TRACK_ID_0)
            assertContains(
                state.value.events, TracksListUiEvent.OpenContextMenu(
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
                state.value.events, TracksListUiEvent.OpenContextMenu(
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
                state.value.events, TracksListUiEvent.OpenContextMenu(
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
            preferencesRepository.modifyTrackListSortOptions(
                mediaSortSettings = mediaSortSettings {
                    sortOption = MediaSortOption.TRACK
                    sortOrder = MediaSortOrder.DESCENDING
                },
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = TracksListViewModel.ALL_TRACKS
            )
            assertEquals(MediaSortOption.TRACK, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)


            preferencesRepository.modifyTrackListSortOptions(
                mediaSortSettings = mediaSortSettings {
                    sortOption = MediaSortOption.ALBUM
                    sortOrder = MediaSortOrder.DESCENDING
                },
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)

            preferencesRepository.modifyTrackListSortOptions(
                mediaSortSettings = mediaSortSettings {
                    sortOption = MediaSortOption.ALBUM
                    sortOrder = MediaSortOrder.ASCENDING
                },
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            assertEquals(MediaSortOption.ALBUM, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)

            preferencesRepository.modifyTrackListSortOptions(
                mediaSortSettings = mediaSortSettings {
                    sortOption = MediaSortOption.ARTIST
                    sortOrder = MediaSortOrder.ASCENDING
                },
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.ASCENDING, state.value.sortOrder)


            preferencesRepository.modifyTrackListSortOptions(
                mediaSortSettings = mediaSortSettings {
                    sortOption = MediaSortOption.ARTIST
                    sortOrder = MediaSortOrder.DESCENDING
                },
                tracksListType = TracksListType.ALL_TRACKS,
                tracksListName = ""
            )
            assertEquals(MediaSortOption.ARTIST, state.value.currentSort)
            assertEquals(MediaSortOrder.DESCENDING, state.value.sortOrder)
        }
    }

    @Test
    fun `onUpButtonClicked adds NavigateUp event`() {
        with(generateViewModel()) {
            onUpButtonClicked()
            assertContains(state.value.events, TracksListUiEvent.NavigateUp)
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
