package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.player.BrowseTree
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

class AlbumsListViewModelTest : BaseViewModelTest() {


    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): AlbumsListViewModel {
        return AlbumsListViewModel(
            musicServiceConnection = musicServiceConnection,
            initialState = mock()
        )
    }

    @Test
    fun `init connects to service for album roots`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.ALBUMS_ROOT),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectedUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_GID, albumGid)
                assertEquals(ALBUM_NAME, albumName)
            }
            handle(AlbumsListUserAction.AlbumClicked(ALBUM_GID, ALBUM_NAME))
        }
    }

    companion object {
        private const val ALBUM_GID = "ALBUM_GID"
        private const val ALBUM_NAME = "ALBUM_NAME"
    }
}