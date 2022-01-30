package com.sebastianvm.musicplayer.ui.library.tracks

import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.GenreBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.preferences.FakePreferencesRepository
import com.sebastianvm.musicplayer.repository.queue.FakeMediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
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
        genreName: String? = null,
    ): TracksListViewModel {
        return TracksListViewModel(
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = TracksListState(
                tracksListTitle = genreName,
                tracksList = listOf(),
                currentSort = SortOption.ARTIST_NAME,
                sortOrder = SortOrder.DESCENDING
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
                            trackId = TrackBuilder.DEFAULT_TRACK_ID,
                            trackName = TrackBuilder.DEFAULT_TRACK_NAME,
                            artists = ArtistBuilder.DEFAULT_ARTIST_NAME,
                            albumName = AlbumBuilder.DEFAULT_ALBUM_NAME,
                            trackNumber = null
                        ),
                        TrackRowState(
                            trackId = TrackBuilder.SECONDARY_TRACK_ID,
                            trackName = TrackBuilder.SECONDARY_TRACK_NAME,
                            artists = ArtistBuilder.SECONDARY_ARTIST_NAME,
                            albumName = AlbumBuilder.SECONDARY_ALBUM_NAME,
                            trackNumber = null
                        ),
                    ), tracksList
                )
                assertEquals(SortOption.TRACK_NAME, currentSort)
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init for genre sets initial state`() = runTest {
        with(generateViewModel(genreName = GenreBuilder.DEFAULT_GENRE_NAME)) {
            delay(1)
            with(state.value) {
                assertEquals(GenreBuilder.DEFAULT_GENRE_NAME, tracksListTitle)
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TrackBuilder.DEFAULT_TRACK_ID,
                            trackName = TrackBuilder.DEFAULT_TRACK_NAME,
                            artists = ArtistBuilder.DEFAULT_ARTIST_NAME,
                            albumName = AlbumBuilder.DEFAULT_ALBUM_NAME,
                            trackNumber = null
                        ),
                    ), tracksList
                )
                assertEquals(SortOption.TRACK_NAME, currentSort)
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for all tracks triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TrackBuilder.DEFAULT_TRACK_ID))
            delay(1)
            verify {
                mediaPlaybackRepository.playFromId(
                    TrackBuilder.DEFAULT_TRACK_ID,
                    MediaGroup(mediaGroupType = MediaType.TRACK, mediaId = "")
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for genre triggers playback, adds nav to player event`() = runTest {
        with(generateViewModel(genreName = GenreBuilder.DEFAULT_GENRE_NAME)) {
            expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TrackBuilder.DEFAULT_TRACK_ID))
            delay(1)
            verify {
                mediaPlaybackRepository.playFromId(
                    TrackBuilder.DEFAULT_TRACK_ID,
                    MediaGroup(
                        mediaGroupType = MediaType.GENRE,
                        mediaId = GenreBuilder.DEFAULT_GENRE_NAME
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
                    preferencesRepository = FakePreferencesRepository(trackSortOption = SortOption.ARTIST_NAME),
                    genreName = GenreBuilder.DEFAULT_GENRE_NAME,
                )
            ) {
                expectUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
                handle(TracksListUserAction.TrackClicked(TrackBuilder.DEFAULT_TRACK_ID))
                delay(1)
                verify {
                    mediaPlaybackRepository.playFromId(
                        TrackBuilder.DEFAULT_TRACK_ID,
                        MediaGroup(
                            mediaGroupType = MediaType.GENRE,
                            mediaId = GenreBuilder.DEFAULT_GENRE_NAME
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
    fun `SortOptionClicked changes state`() = runTest {
        val tracksList = listOf(
            TrackRowState(
                trackId = TrackBuilder.DEFAULT_TRACK_ID,
                trackName = TrackBuilder.DEFAULT_TRACK_NAME,
                artists = ArtistBuilder.DEFAULT_ARTIST_NAME,
                albumName = AlbumBuilder.DEFAULT_ALBUM_NAME,
                trackNumber = null
            ),
            TrackRowState(
                trackId = TrackBuilder.SECONDARY_TRACK_ID,
                trackName = TrackBuilder.SECONDARY_TRACK_NAME,
                artists = ArtistBuilder.SECONDARY_ARTIST_NAME,
                albumName = AlbumBuilder.SECONDARY_ALBUM_NAME,
                trackNumber = null
            )
        )
        with(generateViewModel()) {
            delay(1)

            handle(TracksListUserAction.SortOptionClicked(SortOption.ARTIST_NAME))
            delay(1)
            assertEquals(SortOption.ARTIST_NAME, state.value.currentSort)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.SortOptionClicked(SortOption.ARTIST_NAME))
            delay(1)
            assertEquals(SortOption.ARTIST_NAME, state.value.currentSort)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)

            handle(TracksListUserAction.SortOptionClicked(SortOption.TRACK_NAME))
            delay(1)
            assertEquals(SortOption.TRACK_NAME, state.value.currentSort)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)

            handle(TracksListUserAction.SortOptionClicked(SortOption.TRACK_NAME))
            delay(1)
            assertEquals(SortOption.TRACK_NAME, state.value.currentSort)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.SortOptionClicked(SortOption.ALBUM_NAME))
            delay(1)
            assertEquals(SortOption.ALBUM_NAME, state.value.currentSort)
            assertEquals(SortOrder.ASCENDING, state.value.sortOrder)
            assertEquals(tracksList, state.value.tracksList)

            handle(TracksListUserAction.SortOptionClicked(SortOption.ALBUM_NAME))
            delay(1)
            assertEquals(SortOption.ALBUM_NAME, state.value.currentSort)
            assertEquals(SortOrder.DESCENDING, state.value.sortOrder)
            assertEquals(tracksList.reversed(), state.value.tracksList)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackContextMenuClicked  for all tracks adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<TracksListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(TrackBuilder.DEFAULT_TRACK_ID, trackId)
                assertNull(genreName)
                assertEquals(SortOption.TRACK_NAME, currentSort)
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
            handle(TracksListUserAction.TrackContextMenuClicked(TrackBuilder.DEFAULT_TRACK_ID))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackContextMenuClicked  for genre adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel(genreName = GenreBuilder.DEFAULT_GENRE_NAME)) {
            expectUiEvent<TracksListUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(TrackBuilder.DEFAULT_TRACK_ID, trackId)
                assertEquals(GenreBuilder.DEFAULT_GENRE_NAME, genreName)
                assertEquals(SortOption.TRACK_NAME, currentSort)
                assertEquals(SortOrder.ASCENDING, sortOrder)
            }
            handle(TracksListUserAction.TrackContextMenuClicked(TrackBuilder.DEFAULT_TRACK_ID))
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
}
