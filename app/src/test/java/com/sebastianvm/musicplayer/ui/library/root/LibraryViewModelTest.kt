package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContains

class LibraryViewModelTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private fun generateViewModel(showPermissionExplanationDialog: Boolean = false): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                libraryItems = listOf(
                    LibraryItem.Tracks(count = 0),
                    LibraryItem.Artists(count = 0),
                    LibraryItem.Albums(count = 0),
                    LibraryItem.Genres(count = 0)
                ),
                showPermissionExplanationDialog = showPermissionExplanationDialog,
                showPermissionDeniedDialog = false,
                events = listOf()
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

    @Test
    fun `FabClicked with permission granted starts music scan service`() {
        with(generateViewModel()) {
            onFabClicked(PERMISSION_GRANTED)
            assertContains(state.value.events, LibraryUiEvent.StartGetMusicService)

        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `FabClicked with should show permission explanation dialog changes state`() = runTest {
        with(generateViewModel()) {
            onFabClicked(SHOULD_SHOW_EXPLANATION)
            assertTrue(state.value.showPermissionExplanationDialog)
        }
    }

    @Test
    fun `FabClicked with should request permission adds request permission event`() {
        with(generateViewModel()) {
            onFabClicked(SHOULD_REQUEST_PERMISSION)
            assertContains(state.value.events, LibraryUiEvent.RequestPermission)
        }
    }

    @Test
    fun `RowClicked adds nav NavigateToScreen event`() {
        with(generateViewModel()) {
            onRowClicked(NavRoutes.TRACKS_ROOT)
            assertContains(
                state.value.events,
                LibraryUiEvent.NavigateToScreen(rowId = NavRoutes.TRACKS_ROOT)
            )
        }
    }

    @Test
    fun `PermissionGranted adds StartGetMusicService event`() {
        with(generateViewModel()) {
            onPermissionGranted()
            assertContains(state.value.events, LibraryUiEvent.StartGetMusicService)

        }
    }

    @Test
    fun `PermissionDenied changes state when should show explanation`() {
        with(generateViewModel()) {
           onPermissionDenied(SHOULD_SHOW_EXPLANATION)
            assertTrue(state.value.showPermissionExplanationDialog)
        }
    }

    @Test
    fun `PermissionDenied changes state when should not show explanation`() {
        with(generateViewModel()) {
            onPermissionDenied(SHOULD_REQUEST_PERMISSION)
            assertTrue(state.value.showPermissionDeniedDialog)
        }
    }

    @Test
    fun `DismissPermissionDeniedDialog changes state`() {
        with(generateViewModel(showPermissionExplanationDialog = true)) {
            onDismissPermissionDeniedDialog()
            assertFalse(state.value.showPermissionDeniedDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `PermissionDeniedConfirmButtonClicked adds OpenAppSettings event`() = runTest {
        with(generateViewModel()) {
            onPermissionDeniedConfirmButtonClicked()
            assertContains(state.value.events, LibraryUiEvent.OpenAppSettings)
        }
    }

    @Test
    fun `DismissPermissionExplanationDialog changes state`() {
        with(generateViewModel(showPermissionExplanationDialog = true)) {
           onDismissPermissionExplanationDialog()
            assertFalse(state.value.showPermissionExplanationDialog)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `PermissionExplanationDialogContinueClicked changes state, adds RequestPermission event`() {
        with(generateViewModel(showPermissionExplanationDialog = true)) {
            onPermissionExplanationDialogContinueClicked()
            assertFalse(state.value.showPermissionExplanationDialog)
            assertContains(state.value.events, LibraryUiEvent.RequestPermission)
        }
    }

}
