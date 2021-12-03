package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.player.BrowseTree
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.ui.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class LibraryViewModelTest {
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

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
            expectUiEvent<LibraryUiEvent.StartGetMusicService>(this@runTest)
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
            expectUiEvent<LibraryUiEvent.RequestPermission>(this@runTest)
            handle(LibraryUserAction.FabClicked(SHOULD_REQUEST_PERMISSION))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `RowClicked adds nav NavigateToScreen event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<LibraryUiEvent.NavigateToScreen>(this@runTest) {
                assertEquals(ROW_ID, rowGid)
            }
            handle(LibraryUserAction.RowClicked(ROW_ID))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `PermissionGranted adds StartGetMusicService event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<LibraryUiEvent.StartGetMusicService>(this@runTest)
            handle(LibraryUserAction.PermissionGranted)
        }
    }

    @Test
    fun `PermissionDenied changes state when should show explanation`() {
        with(generateViewModel()) {
            handle(LibraryUserAction.PermissionDenied(SHOULD_SHOW_EXPLANATION))
            assertTrue(state.value.showPermissionExplanationDialog)
        }
    }

    @Test
    fun `PermissionDenied changes state when should not show explanation`() {
        with(generateViewModel()) {
            handle(LibraryUserAction.PermissionDenied(SHOULD_REQUEST_PERMISSION))
            assertTrue(state.value.showPermissionDeniedDialog)
        }
    }

    @Test
    fun `DismissPermissionDeniedDialog changes state`() {
        with(generateViewModel()) {
            setState {
                copy(
                    showPermissionDeniedDialog = true
                )
            }
            handle(LibraryUserAction.DismissPermissionDeniedDialog)
            assertFalse(state.value.showPermissionDeniedDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `PermissionDeniedConfirmButtonClicked adds OpenAppSettings event`() = runTest {
        with(generateViewModel()) {
            expectUiEvent<LibraryUiEvent.OpenAppSettings>(this@runTest)
            handle(LibraryUserAction.PermissionDeniedConfirmButtonClicked)
        }
    }

    @Test
    fun `DismissPermissionExplanationDialog changes state`() {
        with(generateViewModel()) {
            setState {
                copy(
                    showPermissionExplanationDialog = true
                )
            }
            handle(LibraryUserAction.DismissPermissionExplanationDialog)
            assertFalse(state.value.showPermissionExplanationDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `PermissionExplanationDialogContinueClicked changes state, adds RequestPermission event`() = runTest {
        with(generateViewModel()) {
            setState {
                copy(
                    showPermissionExplanationDialog = true
                )
            }
            expectUiEvent<LibraryUiEvent.RequestPermission>(this@runTest)
            handle(LibraryUserAction.PermissionExplanationDialogContinueClicked)
            assertFalse(state.value.showPermissionExplanationDialog)
        }
    }




    companion object {
        private const val ROW_ID = "ROW_ID"
    }
}