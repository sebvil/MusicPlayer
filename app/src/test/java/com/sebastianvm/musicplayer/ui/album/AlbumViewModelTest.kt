package com.sebastianvm.musicplayer.ui.album

import com.sebastianvm.musicplayer.database.entities.fullAlbumInfo
import com.sebastianvm.musicplayer.database.entities.fullTrackInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.FakeMediaQueueRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.uri.FakeUriUtilsRule
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlbumViewModelTest {

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository
    private lateinit var mediaQueueRepository: MediaQueueRepository
    private lateinit var albumRepository: AlbumRepository
    private lateinit var trackRepository: TrackRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    @get:Rule
    val fakeUriUtilsRule = FakeUriUtilsRule()

    @Before
    fun setUp() {
        mediaPlaybackRepository = spyk(FakeMediaPlaybackRepository())
        mediaQueueRepository = spyk(FakeMediaQueueRepository())
        albumRepository = FakeAlbumRepository(
            fullAlbumInfo = listOf(
                fullAlbumInfo {
                    album {
                        albumId = ALBUM_ID
                        albumName = ALBUM_NAME
                        year = ALBUM_YEAR
                        artists = ALBUM_ARTIST
                    }
                    artistIds {
                        add(ALBUM_ARTIST)
                    }
                    trackIds {
                        add(TRACK_ID)
                    }
                })
        )
        trackRepository = FakeTrackRepository(tracks = listOf(
            fullTrackInfo {
                track {
                    trackId = TRACK_ID
                    trackName = TRACK_NAME
                    albumName = ALBUM_NAME
                    trackNumber = TRACK_NUMBER
                    artists = ALBUM_ARTIST
                }
            }
        ))
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = AlbumState(
                albumId = ALBUM_ID,
                tracksList = listOf(),
                albumName = "",
                imageUri = "",
            ),
            albumRepository = albumRepository,
            trackRepository = trackRepository,
            mediaQueueRepository = mediaQueueRepository,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets albumHeaderItem and tracksList`() = runTest {
        with(generateViewModel()) {
            assertEquals(ALBUM_NAME, state.value.albumName)
            assertEquals(
                "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${ALBUM_ID}",
                state.value.imageUri
            )
            assertEquals(
                listOf(
                    TrackRowState(
                        trackId = TRACK_ID,
                        trackName = TRACK_NAME,
                        artists = ALBUM_ARTIST,
                        albumName = ALBUM_NAME,
                        trackNumber = TRACK_NUMBER
                    )
                ), state.value.tracksList
            )
        }
    }

    @Test
    fun `onTrackClicked creates queue, triggers playback adds nav to player event`() {
        with(generateViewModel()) {
            onTrackClicked(TRACK_ID)
            assertEquals(listOf(AlbumUiEvent.NavigateToPlayer), events)
            verify {
                mediaPlaybackRepository.playFromId(
                    TRACK_ID,
                    MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = ALBUM_ID
                    )
                )
            }

            coVerify {
                mediaQueueRepository.createQueue(
                    MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = state.value.albumId
                    ),
                )

            }
        }
    }

    @Test
    fun `onTrackOverflowMenuIconClicked adds OpenContextMenu UiEvent`() {
        with(generateViewModel()) {
            onTrackOverflowMenuIconClicked(TRACK_ID)
            assertEquals(
                listOf(AlbumUiEvent.OpenContextMenu(trackId = TRACK_ID, albumId = ALBUM_ID)), events
            )
        }
    }


    companion object {
        private const val ALBUM_ID = "0"
        private const val ALBUM_NAME = "ALBUM_NAME"
        private const val ALBUM_ARTIST = "ALBUM_ARTIST"
        private const val ALBUM_YEAR = 2000L
        private const val TRACK_ID = "0"
        private const val TRACK_NAME = "TRACK_NAME"
        private const val TRACK_NUMBER = 1L
    }
}
