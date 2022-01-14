package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ArtistsListViewModelTest  {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): ArtistsListViewModel {
        return ArtistsListViewModel(
            initialState = mock(),
            artistRepository = mock(),
            preferencesRepository = mock(),
        )
    }

    @Test
    fun `init connects to service for artists root`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.ARTISTS_ROOT),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `ArtistClicked adds NavigateToArtist event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistsListUiEvent.NavigateToArtist>(this@runTest) {
                Assert.assertEquals(ARTIST_ID, artistId)
            }
            handle(ArtistsListUserAction.ArtistClicked(ARTIST_ID))
        }
    }

    companion object {
        private const val ARTIST_ID = "ARTIST_ID"
    }
}
