package com.sebastianvm.musicplayer.ui.artist

import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ArtistViewModelTest : BaseViewModelTest() {

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): ArtistViewModel {
        return ArtistViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = ArtistState(
                artistHeaderItem = mock(),
                artistGid = ARTIST_GID,
                albumsForArtistItems = listOf(),
                appearsOnForArtistItems = listOf(),
            )
        )
    }

    @Test
    fun `init connects to service for artists`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq("artist-${ARTIST_GID}"),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectedUiEvent<ArtistUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_GID, albumGid)
                assertEquals(ALBUM_NAME, albumName)
            }
            handle(ArtistUserAction.AlbumClicked(ALBUM_GID, ALBUM_NAME))
        }
    }

    companion object {
        private const val ARTIST_GID = "ARTIST_GID"
        private const val ALBUM_GID = "ALBUM_GID"
        private const val ALBUM_NAME = "ALBUM_NAME"
    }
}