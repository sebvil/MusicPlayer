package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ArtistsListViewModelTest : BaseViewModelTest() {
    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): ArtistsListViewModel {
        return ArtistsListViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = mock()
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
            expectedUiEvent<ArtistsListUiEvent.NavigateToArtist>(this@runTest) {
                Assert.assertEquals(ARTIST_GID, artistGid)
                Assert.assertEquals(ARTIST_NAME, artistName)
            }
            handle(ArtistsListUserAction.ArtistClicked(ARTIST_GID, ARTIST_NAME))
        }
    }

    companion object {
        private const val ARTIST_GID = "ARTIST_GID"
        private const val ARTIST_NAME = "ARTIST_NAME"
    }
}