package com.sebastianvm.musicplayer.ui.library.albums

import com.sebastianvm.musicplayer.ui.util.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class AlbumsListViewModelTest : BaseViewModelTest() {


    private fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(mock(), mock())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `AlbumClicked adds NavigateToAlbum event`() = runTest {
        with(generateViewModel()) {
            expectedUiEvent<AlbumsListUiEvent.NavigateToAlbum>(this@runTest) {
                assertEquals(ALBUM_GID, it.albumGid)
                assertEquals(ALBUM_NAME, it.albumName)
            }
            handle(AlbumsListUserAction.AlbumClicked(ALBUM_GID, ALBUM_NAME))
        }
    }

    companion object {
        private const val ALBUM_GID = "ALBUM_GID"
        private const val ALBUM_NAME = "ALBUM_NAME"
    }
}