package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.player.BrowseTree
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

class AlbumsListViewModelTest  {

    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(
            initialState = mock(),
            albumRepository = mock()
        )
    }

    @Test
    fun `init connects to service for album roots`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel()
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.ALBUMS_ROOT),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_GID, albumGid)
            }
            handle(AlbumsListUserAction.AlbumClicked(ALBUM_GID))
        }
    }

    companion object {
        private const val ALBUM_GID = "ALBUM_GID"
        private const val ALBUM_NAME = "ALBUM_NAME"
    }
}