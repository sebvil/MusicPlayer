package com.sebastianvm.musicplayer.ui.artist

import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ArtistViewModelTest  {
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): ArtistViewModel {
        return ArtistViewModel(
            initialState = ArtistState(
                artistHeaderItem = mock(),
                artistId = ARTIST_ID,
                albumsForArtistItems = listOf(),
                appearsOnForArtistItems = listOf(),
            ),
            albumRepository = mock(),
            artistRepository = mock()
        )
    }

    @Test
    fun `init connects to service for artists`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq("artist-${ARTIST_ID}"),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<ArtistUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_ID, albumId)
            }
            handle(ArtistUserAction.AlbumClicked(ALBUM_ID))
        }
    }

    companion object {
        private const val ARTIST_ID = "ARTIST_ID"
        private const val ALBUM_ID = "ALBUM_ID"
        private const val ALBUM_NAME = "ALBUM_NAME"
    }
}