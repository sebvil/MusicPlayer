package com.sebastianvm.musicplayer.ui.album

import android.content.ContentUris
import android.provider.MediaStore
import android.support.v4.media.session.MediaControllerCompat
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.database.entities.AlbumBuilder
import com.sebastianvm.musicplayer.database.entities.ArtistBuilder
import com.sebastianvm.musicplayer.database.entities.TrackBuilder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.expectUiEvent
import io.mockk.Runs
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumViewModelTest {

    private val albumRepository: AlbumRepository = FakeAlbumRepository()
    private lateinit var musicServiceConnection: MusicServiceConnection
    private lateinit var mediaQueueRepository: MediaQueueRepository

    @Before
    fun setUp() {
        musicServiceConnection = mockk()
        mediaQueueRepository = mockk()
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = AlbumState(
                albumId = AlbumBuilder.PRIMARY_ALBUM_ID,
                tracksList = listOf(),
                albumHeaderItem = mockk()
            ),
            albumRepository = albumRepository,
            mediaQueueRepository = mediaQueueRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets albumHeaderItem and tracksList`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(
                    HeaderWithImageState(
                        title = DisplayableString.StringValue(AlbumBuilder.PRIMARY_ALBUM_NAME),
                        image = MediaArt(
                            uris = listOf(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, AlbumBuilder.PRIMARY_ALBUM_ID.toLong()
                                )
                            ),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf(AlbumBuilder.PRIMARY_ALBUM_NAME)
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
                        )
                    ), state.value.albumHeaderItem
                )
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TrackBuilder.DEFAULT_TRACK_ID,
                            trackName = TrackBuilder.DEFAULT_TRACK_NAME,
                            artists = ArtistBuilder.PRIMARY_ARTIST_NAME,
                            albumName = AlbumBuilder.PRIMARY_ALBUM_NAME,
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
        val transportControls: MediaControllerCompat.TransportControls = mockk()
        every { transportControls.playFromMediaId(any(), any()) } just Runs
        every { musicServiceConnection.transportControls } returns transportControls
        coJustRun { mediaQueueRepository.createQueue(any(), any(), any()) }

        with(generateViewModel()) {
            expectUiEvent<AlbumUiEvent.NavigateToPlayer>(this@runTest)
            handle(AlbumUserAction.TrackClicked(TrackBuilder.DEFAULT_TRACK_ID))
            io.mockk.verify {
                transportControls.playFromMediaId(any(), any())
            }


            coVerify {
                mediaQueueRepository.createQueue(
                    MediaGroup(
                        mediaType = MediaType.ALBUM,
                        mediaId = state.value.albumId
                    ),
                    SortOption.TRACK_NUMBER,
                    SortOrder.ASCENDING
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
                assertEquals(AlbumBuilder.PRIMARY_ALBUM_ID, albumId)
            }
            handle(AlbumUserAction.TrackContextMenuClicked(TrackBuilder.DEFAULT_TRACK_ID))
        }
    }

   
}
