package com.sebastianvm.musicplayer.ui.album

import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AlbumViewModelTest {

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): AlbumViewModel {
        return AlbumViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = AlbumState(
                albumGid = ALBUM_GID,
                tracksList = listOf(),
                albumHeaderItem = mock()
            )
        )
    }

    @Test
    fun `init connects to service for album`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq("album-$ALBUM_GID"),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for  triggers playback, adds nav to player event`() = runTest {
        val musicServiceConnection: MusicServiceConnection = mock {
            on { transportControls } doReturn mock()
        }

        with(generateViewModel(musicServiceConnection)) {
            expectUiEvent<AlbumUiEvent.NavigateToPlayer>(this@runTest)
            handle(AlbumUserAction.TrackClicked(TRACK_GID))
            verify(musicServiceConnection.transportControls).playFromMediaId(
                eq(TRACK_GID),
                check {
                    assertEquals("album-$ALBUM_GID", it.getString(PARENT_ID))
                    assertEquals(
                        MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                        it.getString(SORT_BY)
                    )
                }
            )
        }
    }

    companion object {
        const val TRACK_GID = "TRACK_GID"
        const val ALBUM_GID = "ALBUM_GID"
    }
}