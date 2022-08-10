//package com.sebastianvm.musicplayer.ui.library.root
//
//import com.sebastianvm.musicplayer.player.TrackListType
//import com.sebastianvm.musicplayer.repository.music.FakeMusicRepository
//import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
//import com.sebastianvm.musicplayer.ui.library.tracks.TrackListViewModel
//import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
//import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
//import com.sebastianvm.musicplayer.util.DispatcherSetUpRule
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//import kotlin.test.assertContains
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class LibraryViewModelTest {
//
//    @get:Rule
//    val dispatcherSetUpRule = DispatcherSetUpRule()
//
//    private fun generateViewModel(): LibraryViewModel {
//        return LibraryViewModel(
//            initialState = LibraryState(
//                libraryItems = listOf(
//                    LibraryItem.Tracks(count = 0),
//                    LibraryItem.Artists(count = 0),
//                    LibraryItem.Albums(count = 0),
//                    LibraryItem.Genres(count = 0),
//                    LibraryItem.Playlists(count = 0)
//                ),
//            ),
//            musicRepository = FakeMusicRepository(),
//        )
//    }
//
//    @Test
//    fun `init updates counts`() = runTest {
//        with(generateViewModel()) {
//            advanceUntilIdle()
//            assertEquals(
//                listOf(
//                    LibraryItem.Tracks(count = FakeMusicRepository.FAKE_TRACK_COUNTS),
//                    LibraryItem.Artists(count = FakeMusicRepository.FAKE_ARTIST_COUNTS),
//                    LibraryItem.Albums(count = FakeMusicRepository.FAKE_ALBUM_COUNTS),
//                    LibraryItem.Genres(count = FakeMusicRepository.FAKE_GENRE_COUNTS),
//                    LibraryItem.Playlists(count = FakeMusicRepository.FAKE_PLAYLIST_COUNTS)
//                ), state.value.libraryItems
//            )
//        }
//    }
//
//
//    @Test
//    fun `onRowClicked adds nav NavigateToScreen event`() {
//        with(generateViewModel()) {
//            onRowClicked(NavigationRoute.TrackList)
//            assertContains(
//                events.value,
//                LibraryUiEvent.NavEvent(
//                    NavigationDestination.TrackList(
//                        TrackListArguments(
//                            trackListType = TrackListType.ALL_TRACKS,
//                            trackListId = TrackListViewModel.ALL_TRACKS
//                        )
//                    )
//                )
//            )
//        }
//    }
//
//}
