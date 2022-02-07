package com.sebastianvm.musicplayer.ui.album

import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.playback.FakeMediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.FakeMediaQueueRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.uri.FakeUriUtilsRule
import io.mockk.coVerify
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlbumViewModelTest {

    private lateinit var mediaPlaybackRepository: MediaPlaybackRepository
    private lateinit var mediaQueueRepository: MediaQueueRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainCoroutineRule = DispatcherSetUpRule()

    @get:Rule
    val fakeUriUtilsRule = FakeUriUtilsRule()

    @Before
    fun setUp() {
        mediaPlaybackRepository = spyk(FakeMediaPlaybackRepository())
        mediaQueueRepository = spyk(FakeMediaQueueRepository())
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            mediaPlaybackRepository = mediaPlaybackRepository,
            initialState = AlbumState(
                albumId = AlbumBuilder.DEFAULT_ALBUM_ID,
                tracksList = listOf(),
                albumName = "",
                imageUri = ""
            ),
            albumRepository = FakeAlbumRepository(),
            mediaQueueRepository = mediaQueueRepository,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets albumHeaderItem and tracksList`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_NAME, state.value.albumName)
                assertEquals(
                    "${FakeUriUtilsRule.FAKE_ALBUM_PATH}/${AlbumBuilder.DEFAULT_ALBUM_ID}",
                    state.value.imageUri
                )
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TrackBuilder.DEFAULT_TRACK_ID,
                            trackName = TrackBuilder.DEFAULT_TRACK_NAME,
                            artists = ArtistBuilder.DEFAULT_ARTIST_NAME,
                            albumName = AlbumBuilder.DEFAULT_ALBUM_NAME,
                            trackNumber = TrackBuilder.DEFAULT_TRACK_NUMBER
                        )
                    ), state.value.tracksList
                )
            }
            delay(1)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked creates queue, triggers playback adds nav to player event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumUiEvent.NavigateToPlayer>(this@runTest)
            handle(AlbumUserAction.TrackClicked(TrackBuilder.DEFAULT_TRACK_ID))
            delay(1)
            verify {
                mediaPlaybackRepository.playFromId(
                    TrackBuilder.DEFAULT_TRACK_ID,
                    MediaGroup(mediaGroupType = MediaGroupType.ALBUM, mediaId = AlbumBuilder.DEFAULT_ALBUM_ID)
                )
            }


            coVerify {
                mediaQueueRepository.createQueue(
                    MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = state.value.albumId
                    ),
                    MediaSortOption.TRACK_NUMBER,
                    MediaSortOrder.ASCENDING
                )

            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackContextMenuClicked adds OpenContextMenu UiEvent`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumUiEvent.OpenContextMenu>(this@runTest) {
                assertEquals(TrackBuilder.DEFAULT_TRACK_ID, trackId)
                assertEquals(AlbumBuilder.DEFAULT_ALBUM_ID, albumId)
            }
            handle(AlbumUserAction.TrackContextMenuClicked(TrackBuilder.DEFAULT_TRACK_ID))
        }
    }


}
