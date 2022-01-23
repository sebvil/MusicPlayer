package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LibraryViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = 0),
                    LibraryItem.Artists(count = 0),
                    LibraryItem.Albums(count = 0),
                    LibraryItem.Genres(count = 0)
                ),
                showPermissionExplanationDialog = false,
                showPermissionDeniedDialog = false,
            ),
            musicRepository = FakeMusicRepository()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init updates counts`() = runTest {
        with(generateViewModel()) {
            delay(1)
            assertEquals(
                listOf(
                    LibraryItem.Tracks(count = 1000),
                    LibraryItem.Artists(count = 10),
                    LibraryItem.Albums(count = 100),
                    LibraryItem.Genres(count = 1)
                ), state.value.libraryItems
            )
        }
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
            assertTrue(state.value.showPermissionExplanationDialog)
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
                assertEquals(NavRoutes.TRACKS_ROOT, rowId)
            }
            handle(LibraryUserAction.RowClicked(NavRoutes.TRACKS_ROOT))
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
    fun `PermissionExplanationDialogContinueClicked changes state, adds RequestPermission event`() =
        runTest {
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

}
