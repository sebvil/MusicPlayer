package com.sebastianvm.musicplayer.ui.album

import android.content.ContentUris
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.database.entities.Album
import com.sebastianvm.musicplayer.database.entities.Artist
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumViewModelTest {

    private lateinit var albumRepository: AlbumRepository

    @Before
    fun setUp() {
        albumRepository = mock()
        val album = Album(
            albumId = ALBUM_ID,
            albumName = ALBUM_NAME,
            year = ALBUM_YEAR,
            numberOfTracks = NUMBER_OF_TRACKS
        )
        whenever(albumRepository.getAlbumWithTracks(any())).doReturn(flow {
            emit(
                mapOf(
                    album to listOf(
                        FullTrackInfo(
                            track = mock {
                                on { trackId } doReturn TRACK_ID
                                on { trackName } doReturn TRACK_NAME
                                on { trackNumber } doReturn TRACK_NUMBER
                            },
                            artists = listOf(
                                Artist(
                                    artistId = ARTIST_NAME,
                                    artistName = ARTIST_NAME
                                )
                            ),
                            album = album,
                            genres = listOf()
                        )
                    )
                )
            )
        })
    }

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): AlbumViewModel {
        return AlbumViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = AlbumState(
                albumId = ALBUM_ID,
                tracksList = listOf(),
                albumHeaderItem = mock()
            ),
            albumRepository = albumRepository,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init sets albumHeaderItem and tracksList`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(
                    HeaderWithImageState(
                        title = DisplayableString.StringValue(ALBUM_NAME), image = MediaArt(
                            uris = listOf(
                                ContentUris.withAppendedId(
                                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, ALBUM_ID.toLong()
                                )
                            ),
                            contentDescription =  DisplayableString.ResourceValue(
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
    fun `TrackClicked for  triggers playback, adds nav to player event`() = runTest {
        val musicServiceConnection: MusicServiceConnection = mock {
            on { transportControls } doReturn mock()
        }

        with(generateViewModel(musicServiceConnection)) {
            expectUiEvent<AlbumUiEvent.NavigateToPlayer>(this@runTest)
            handle(AlbumUserAction.TrackClicked(TRACK_ID))
            verify(musicServiceConnection.transportControls).playFromMediaId(
                eq(TRACK_ID),
                check {
                    assertEquals(
                        MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                        it.getString(SORT_BY)
                    )
                }
            )
        }
    }

    companion object {
        const val TRACK_ID = "TRACK_ID"
        const val TRACK_NAME = "TRACK_NAME"
        const val ARTIST_NAME = "ARTIST_NAME"
        const val TRACK_NUMBER = 10L
        const val ALBUM_ID = "100"
        const val ALBUM_NAME = "ALBUM_NAME"
        const val ALBUM_YEAR = 2020L
        const val NUMBER_OF_TRACKS = 15L
    }
}