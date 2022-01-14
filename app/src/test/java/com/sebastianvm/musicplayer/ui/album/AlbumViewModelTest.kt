package com.sebastianvm.musicplayer.ui.album

import android.content.ContentUris
import android.provider.MediaStore
import android.support.v4.media.session.MediaControllerCompat
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.util.expectUiEvent
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import io.mockk.Runs
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumViewModelTest {

    private lateinit var albumRepository: AlbumRepository
    private lateinit var musicServiceConnection: MusicServiceConnection
    private lateinit var mediaQueueRepository: MediaQueueRepository

    @Before
    fun setUp() {
        musicServiceConnection = mockk()
        albumRepository = mockk()
        mediaQueueRepository = mockk()
        val album = Album(
            albumId = ALBUM_ID,
            albumName = ALBUM_NAME,
            year = ALBUM_YEAR,
            numberOfTracks = NUMBER_OF_TRACKS
        )
        every { albumRepository.getAlbumWithTracks(any()) } returns flow {
            emit(
                mapOf(
                    album to listOf(
                        FullTrackInfo(
                            track = mockk {
                                every { trackId } returns  TRACK_ID
                                every { trackName } returns  TRACK_NAME
                                every { trackNumber } returns  TRACK_NUMBER
                            },
                            artists = listOf(
                                Artist(
                                    artistName = ARTIST_NAME,
                                    artistName = ARTIST_NAME
                                )
                            ),
                            album = album,
                            genres = listOf()
                        )
                    )
                )
            )
        }
    }

    private fun generateViewModel(): AlbumViewModel {
        return AlbumViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = AlbumState(
                albumId = ALBUM_ID,
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
                        title = DisplayableString.StringValue(ALBUM_NAME),
                        image = MediaArt(
                            uris = listOf(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, ALBUM_ID.toLong()
                                )
                            ),
                            contentDescription = DisplayableString.ResourceValue(
                                value = R.string.album_art_for_album,
                                arrayOf(ALBUM_NAME)
                            ),
                            backupResource = R.drawable.ic_album,
                            backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
                        )
                    ), state.value.albumHeaderItem
                )
                assertEquals(
                    listOf(
                        TrackRowState(
                            trackId = TRACK_ID,
                            trackName = TRACK_NAME,
                            artists = ARTIST_NAME,
                            albumName = ALBUM_NAME,
                            trackNumber = TRACK_NUMBER
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
            handle(AlbumUserAction.TrackClicked(TRACK_ID))
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
                assertEquals(TRACK_ID, trackId)
                assertEquals(ALBUM_ID, albumId)
            }
            handle(AlbumUserAction.TrackContextMenuClicked(TRACK_ID))
        }
    }

    companion object {
        private const val TRACK_ID = "TRACK_ID"
        private const val TRACK_NAME = "TRACK_NAME"
        private const val ARTIST_NAME = "ARTIST_NAME"
        private const val TRACK_NUMBER = 10L
        private const val ALBUM_ID = "100"
        private const val ALBUM_NAME = "ALBUM_NAME"
        private const val ALBUM_YEAR = 2020L
        private const val NUMBER_OF_TRACKS = 15L
    }
}
