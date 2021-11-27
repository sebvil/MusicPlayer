package com.sebastianvm.musicplayer.ui.library.tracks

import android.support.v4.media.MediaMetadataCompat
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.ui.util.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class TracksListViewModelTest : BaseViewModelTest() {

    private fun generateViewModel(
        musicServiceConnection: MusicServiceConnection = mock(),
        tracksListTitle: DisplayableString = DisplayableString.ResourceValue(R.string.all_songs)
    ): TracksListViewModel {
        return TracksListViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = TracksListState(
                tracksListTitle = tracksListTitle,
                tracksList = listOf(),
                isSortMenuOpen = false
            )
        )
    }

    @Test
    fun `init connects to service for all tracks`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.TRACKS_ROOT),
            any()
        )
    }

    @Test
    fun `init connects to service for genre`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection, DisplayableString.StringValue(GENRE_NAME))
        verify(musicServiceConnection).subscribe(
            eq("genre-$GENRE_NAME"),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for all tracks triggers playback, adds nav to player event`() = runTest {
        val musicServiceConnection: MusicServiceConnection = mock {
            on { transportControls } doReturn mock()
        }

        with(generateViewModel(musicServiceConnection)) {
            expectedUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TRACK_GID))
            verify(musicServiceConnection.transportControls).playFromMediaId(
                eq(TRACK_GID),
                org.mockito.kotlin.check {
                    assertEquals(BrowseTree.TRACKS_ROOT, it.getString(PARENT_ID))
                    assertEquals(MediaMetadataCompat.METADATA_KEY_TITLE, it.getString(SORT_BY))
                }
            )
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `TrackClicked for genre triggers playback, adds nav to player event`() = runTest {
        val musicServiceConnection: MusicServiceConnection = mock {
            on { transportControls } doReturn mock()
        }
        with(generateViewModel(musicServiceConnection, DisplayableString.StringValue(GENRE_NAME))) {
            expectedUiEvent<TracksListUiEvent.NavigateToPlayer>(this@runTest)
            handle(TracksListUserAction.TrackClicked(TRACK_GID))
            verify(musicServiceConnection.transportControls).playFromMediaId(
                eq(TRACK_GID),
                org.mockito.kotlin.check {
                    assertEquals("genre-$GENRE_NAME", it.getString(PARENT_ID))
                    assertEquals(MediaMetadataCompat.METADATA_KEY_TITLE, it.getString(SORT_BY))
                }
            )
        }
    }

    @Test
    fun `SortByClicked changes state`() {
        with(generateViewModel()) {
            handle(TracksListUserAction.SortByClicked)
            assertTrue(state.value.isSortMenuOpen)
            handle(TracksListUserAction.SortByClicked)
            assertFalse(state.value.isSortMenuOpen)
        }
    }


    companion object {
        private const val GENRE_NAME = "GENRE_NAME"
        private const val TRACK_GID = "TRACK_GID"
    }


}