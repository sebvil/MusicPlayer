package com.sebastianvm.musicplayer.ui.library.root

import com.sebastianvm.musicplayer.PERMISSION_GRANTED
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.SHOULD_REQUEST_PERMISSION
import com.sebastianvm.musicplayer.SHOULD_SHOW_EXPLANATION
import com.sebastianvm.musicplayer.repository.MusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes
import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
import com.sebastianvm.musicplayer.util.expectUiEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LibraryViewModelTest {
    @get:Rule
    val dispatcherSetUpRule = DispatcherSetUpRule()

    private lateinit var musicRepository: MusicRepository

    @Before
    fun setUp() {
        musicRepository = mock()
        whenever(musicRepository.getCounts()).doReturn(flow {
            emit(
                MusicRepository.CountHolder(
                    1000,
                    100,
                    10,
                    1
                )
            )
        })
    }

    private fun generateViewModel(): LibraryViewModel {
        return LibraryViewModel(
            initialState = LibraryState(
                libraryItems = listOf(
                    LibraryItem(
                        rowId = NavRoutes.TRACKS_ROOT,
                        rowName = R.string.all_songs,
                        icon = R.drawable.ic_song,
                        countString = R.plurals.number_of_tracks,
                        count = 0
                    ),
                    LibraryItem(
                        rowId = NavRoutes.ARTISTS_ROOT,
                        rowName = R.string.artists,
                        icon = R.drawable.ic_artist,
                        countString = R.plurals.number_of_artists,
                        count = 0
                    ),
                    LibraryItem(
                        rowId = NavRoutes.ALBUMS_ROOT,
                        rowName = R.string.albums,
                        icon = R.drawable.ic_album,
                        countString = R.plurals.number_of_albums,
                        count = 0
                    ),
                    LibraryItem(
                        rowId = NavRoutes.GENRES_ROOT,
                        rowName = R.string.genres,
                        icon = R.drawable.ic_genre,
                        countString = R.plurals.number_of_genres,
                        count = 0
                    )
                ),
                showPermissionExplanationDialog = false,
                showPermissionDeniedDialog = false,
            ),
            musicRepository = musicRepository
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `init updates counts`() = runTest {
        with(generateViewModel()) {
            launch {
                assertEquals(
                    listOf(
                        LibraryItem(
                            rowId = NavRoutes.TRACKS_ROOT,
                            rowName = R.string.all_songs,
                            icon = R.drawable.ic_song,
                            countString = R.plurals.number_of_tracks,
                            count = 1000
                        ),
                        LibraryItem(
                            rowId = NavRoutes.ARTISTS_ROOT,
                            rowName = R.string.artists,
                            icon = R.drawable.ic_artist,
                            countString = R.plurals.number_of_artists,
                            count = 100
                        ),
                        LibraryItem(
                            rowId = NavRoutes.ALBUMS_ROOT,
                            rowName = R.string.albums,
                            icon = R.drawable.ic_album,
                            countString = R.plurals.number_of_albums,
                            count = 10
                        ),
                        LibraryItem(
                            rowId = NavRoutes.GENRES_ROOT,
                            rowName = R.string.genres,
                            icon = R.drawable.ic_genre,
                            countString = R.plurals.number_of_genres,
                            count = 1
                        )
                    ), state.value.libraryItems

                )
            }
            delay(1)
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
                assertEquals(ROW_ID, rowId)
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


    companion object {
        private const val ROW_ID = "ROW_ID"
    }
}
