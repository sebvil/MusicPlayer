package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.BaseViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LibraryViewModelTest : BaseViewModelTest() {

    private fun generateViewModel(musicServiceConnection: MusicServiceConnection = mock()): LibraryViewModel {
        return LibraryViewModel(
            musicServiceConnection = musicServiceConnection,
            LibraryState(
                libraryItems = listOf(),
                showPermissionExplanationDialog = false,
                showPermissionDeniedDialog = false,
            )
        )
    }

    @Test
    fun `init subscribes to music service`() {
        val musicServiceConnection: MusicServiceConnection = mock()
        generateViewModel(musicServiceConnection = musicServiceConnection)
        verify(musicServiceConnection).subscribe(
            eq(BrowseTree.MEDIA_ROOT),
            any()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `FabClicked with permission granted starts music scan service`() = runTest {
        with(generateViewModel()) {
            expectedUiEvent<LibraryUiEvent.StartGetMusicService>(this@runTest)
            handle(LibraryUserAction.FabClicked(PERMISSION_GRANTED))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `FabClicked with should show permission explanation dialog changes state`() = runTest {
        with(generateViewModel()) {
            handle(LibraryUserAction.FabClicked(SHOULD_SHOW_EXPLANATION))
            launch {
                assertTrue(state.first().showPermissionExplanationDialog)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `FabClicked with should request permission adds request permission event`() = runTest {
        with(generateViewModel()) {
            expectedUiEvent<LibraryUiEvent.RequestPermission>(this@runTest)
            handle(LibraryUserAction.FabClicked(SHOULD_REQUEST_PERMISSION))
        }
    }


}