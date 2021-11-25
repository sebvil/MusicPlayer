package com.sebastianvm.musicplayer.ui.library.albums

import org.junit.Test
import org.mockito.kotlin.mock

class AlbumsListViewModelTest {


    fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(mock(), mock())
    }

    @Test
    fun `trial test`(){
        with(generateViewModel()) {
            handle(AlbumsListUserAction.AlbumClicked("a","a"))
        }
    }
}