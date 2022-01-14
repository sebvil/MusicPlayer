package com.sebastianvm.musicplayer.ui.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.player.EMPTY_PLAYBACK_STATE
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.NOTHING_PLAYING
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.extensions.albumId
import com.sebastianvm.musicplayer.util.extensions.artist
import com.sebastianvm.musicplayer.util.extensions.duration
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.title
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MusicPlayerViewModelTest {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var musicServiceConnection: MusicServiceConnection

    @Before
    fun setUp() {
        musicServiceConnection = mock {
            on { playbackState } doReturn MutableStateFlow(EMPTY_PLAYBACK_STATE)
            on { nowPlaying } doReturn MutableStateFlow(NOTHING_PLAYING)
            on { transportControls } doReturn mock()
        }
    }

    private fun generateViewModel(): MusicPlayerViewModel {
        return MusicPlayerViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = MusicPlayerState(
                isPlaying = false,
                trackName = null,
                artists = null,
                trackLengthMs = null,
                currentPlaybackTimeMs = null,
                trackId = null,
                albumId = null,
                trackArt = MediaArt(
                    uris = listOf(),
                    contentDescription = DisplayableString.StringValue(""),
                    backupResource = R.drawable.ic_album,
                    backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
                )
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state changes when playbackState changes`() = runTest {
        with(generateViewModel()) {
            this@runTest.launch {
                assertTrue(state.drop(2).first().isPlaying)
            }
            delay(1)
            musicServiceConnection.playbackState.value = PlaybackStateCompat.Builder().setState(
                PlaybackStateCompat.STATE_PLAYING, 0, 1f
            ).build()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `state changes when nowPlaying changes`() = runTest {
        with(generateViewModel()) {
            this@runTest.launch {
                with(state.drop(2).first()) {
                    assertEquals(TRACK_TITLE, trackName)
                    assertEquals(ARTISTS, artists)
                    assertEquals(TRACK_LENGTH, trackLengthMs)
                    assertEquals(TRACK_ID, trackId)
                    assertEquals(ALBUM_ID, albumId)
                }
            }
            delay(1)
            musicServiceConnection.nowPlaying.value = MediaMetadataCompat.Builder().apply {
                title = TRACK_TITLE
                artist = ARTISTS
                duration = TRACK_LENGTH
                id = TRACK_ID
                albumId = ALBUM_ID
            }.build()
        }
    }

    @Test
    fun `TogglePlay while playing triggers pause`() {
        whenever(musicServiceConnection.playbackState) doReturn MutableStateFlow(
            PlaybackStateCompat.Builder().setState(
                PlaybackStateCompat.STATE_PLAYING, 0, 1f
            ).build()
        )
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.TogglePlay)
            verify(musicServiceConnection.transportControls).pause()
        }
    }

    @Test
    fun `TogglePlay while paused triggers play`() {
        whenever(musicServiceConnection.playbackState) doReturn MutableStateFlow(
            PlaybackStateCompat.Builder().setState(
                PlaybackStateCompat.STATE_PAUSED, 0, 1f
            ).setActions(PlaybackStateCompat.ACTION_PLAY).build()
        )
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.TogglePlay)
            verify(musicServiceConnection.transportControls).play()
        }
    }

    @Test
    fun `PreviousTapped triggers skipToPrevious`() {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.PreviousTapped)
            verify(musicServiceConnection.transportControls).skipToPrevious()
        }
    }

    @Test
    fun `NextTapped triggers skipToNext`() {
        with(generateViewModel()) {
            handle(MusicPlayerUserAction.NextTapped)
            verify(musicServiceConnection.transportControls).skipToNext()
        }
    }

    companion object {
        private const val TRACK_TITLE = "TRACK_TITLE"
        private const val ARTISTS = "ARTISTS"
        private const val TRACK_LENGTH = 300000L
        private const val TRACK_ID = "11111"
        private const val ALBUM_ID = "22222"
    }


}
