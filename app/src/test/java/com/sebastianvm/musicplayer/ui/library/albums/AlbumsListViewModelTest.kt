package com.sebastianvm.musicplayer.ui.library.albums

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

class AlbumsListViewModelTest {


    private     fun generateViewModel(): AlbumsListViewModel {
        return AlbumsListViewModel(mock(), mock())
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    private val testDispatcher = StandardTestDispatcher()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `trial test`(){
        Dispatchers.setMain(Dispatchers.Unconfined)
        with(generateViewModel()) {
            handle(AlbumsListUserAction.AlbumClicked("a","a"))
            CoroutineScope(Dispatchers.Main).launch {
                eventsFlow.collect {
                    assertTrue(it is AlbumsListUiEvent.NavigateToAlbum)
                    assertTrue(false)
                }
            }
        }
    }
}