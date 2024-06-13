// package com.sebastianvm.musicplayer.features.playlist.tracksearch
//
// import app.cash.turbine.test
// import com.sebastianvm.musicplayer.designsystem.components.TrackRow
// import com.sebastianvm.musicplayer.features.navigation.FakeNavController
// import com.sebastianvm.musicplayer.repository.fts.FakeFullTextSearchRepository
// import com.sebastianvm.musicplayer.repository.playlist.FakePlaylistRepository
// import io.kotest.core.spec.style.FreeSpec
// import io.kotest.core.test.TestScope
// import io.kotest.matchers.shouldBe
// import javax.security.auth.Subject
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.ExperimentalCoroutinesApi
// import kotlinx.coroutines.test.StandardTestDispatcher
// import kotlinx.coroutines.test.resetMain
// import kotlinx.coroutines.test.runTest
// import kotlinx.coroutines.test.setMain
//
// @OptIn(ExperimentalCoroutinesApi::class)
// class TrackSearchStateHolderTest :
//    FreeSpec({
//        lateinit var playlistRepository: FakePlaylistRepository
//        lateinit var ftsRepository: FakeFullTextSearchRepository
//        lateinit var navController: FakeNavController
//
//        beforeTest {
//            playlistRepository = FakePlaylistRepository()
//            ftsRepository = FakeFullTextSearchRepository()
//            navController = FakeNavController()
//        }
//
//        afterTest { Dispatchers.resetMain() }
//
//        fun TestScope.getSubject(): TrackSearchStateHolder {
//            return TrackSearchStateHolder(
//                arguments = TrackSearchArguments(playlistId = PLAYLIST_ID),
//                playlistRepository = playlistRepository,
//                ftsRepository = ftsRepository,
//                navController = navController
//            )
//        }
//
//        "init" -
//            {
//                "subscribes to changes in search results" {
//                    val subject = getSubject()
//                    val initialSearchResults = emptyList<Track>()
//                    ftsRepository.stubSearchResults("", initialSearchResults)
//
//                    runTest {
//                        subject.state.test {
//                            assertThat(awaitItem())
//                                .isEqualTo(TrackSearchState(trackSearchResults = emptyList()))
//
//                            val newSearchResults =
//                                listOf(
//                                    Track(
//                                        id = 1,
//                                        title = "Track 1",
//                                        artist = "Artist 1",
//                                        album = "Album 1"
//                                    ),
//                                    Track(
//                                        id = 2,
//                                        title = "Track 2",
//                                        artist = "Artist 2",
//                                        album = "Album 2"
//                                    )
//                                )
//                            ftsRepository.stubSearchResults("query", newSearchResults)
//                            subject.handle(TrackSearchUserAction.TextChanged("query"))
//
//                            assertThat(awaitItem())
//                                .isEqualTo(
//                                    TrackSearchState(
//                                        trackSearchResults =
//                                            newSearchResults.map {
//                                                TrackSearchResult(
//                                                    state = TrackRow.State.fromTrack(it),
//                                                    inPlaylist = false
//                                                )
//                                            }
//                                    )
//                                )
//
//                            cancelAndIgnoreRemainingEvents()
//                        }
//                    }
//                }
//
//                "subscribes to changes in playlist tracks" {
//                    val subject = getSubject()
//                    val initialSearchResults =
//                        listOf(
//                            Track(id = 1, title = "Track 1", artist = "Artist 1", album = "Album
// 1"),
//                            Track(id = 2, title = "Track 2", artist = "Artist 2", album = "Album
// 2")
//                        )
//                    ftsRepository.stubSearchResults("query", initialSearchResults)
//                    subject.handle(TrackSearchUserAction.TextChanged("query"))
//
//                    runTest {
//                        subject.state.test {
//                            // Skip initial emission
//                            awaitItem()
//
//                            playlistRepository.addTrackToPlaylist(playlistId, 1)
//
//                            assertThat(awaitItem())
//                                .isEqualTo(
//                                    TrackSearchState(
//                                        trackSearchResults =
//                                            listOf(
//                                                TrackSearchResult(
//                                                    state =
//                                                        TrackRow.State.fromTrack(
//                                                            initialSearchResults[0]
//                                                        ),
//                                                    inPlaylist = true
//                                                ),
//                                                TrackSearchResult(
//                                                    state =
//                                                        TrackRow.State.fromTrack(
//                                                            initialSearchResults[1]
//                                                        ),
//                                                    inPlaylist = false
//                                                )
//                                            )
//                                    )
//                                )
//
//                            cancelAndIgnoreRemainingEvents()
//                        }
//                    }
//                }
//            }
//
//        "handle" -
//            {
//                "TrackClicked adds track to playlist and shows toast" {
//                    val subject = getSubject()
//                    val trackId = 1L
//                    val trackName = "Track 1"
//
//                    subject.handle(TrackSearchUserAction.TrackClicked(trackId, trackName))
//
//
// assertThat(playlistRepository.trackIdsInPlaylist(playlistId)).contains(trackId)
//
//                    runTest {
//                        subject.state.test {
//                            awaitItem().trackAddedToPlaylist shouldBe trackName
//                            cancelAndIgnoreRemainingEvents()
//                        }
//                    }
//                }
//
//                "ToastShown hides toast" {
//                    val subject = getSubject()
//
//                    subject.handle(TrackSearchUserAction.TrackClicked(1, "Track 1"))
//                    subject.handle(TrackSearchUserAction.ToastShown)
//
//                    runTest {
//                        subject.state.test {
//                            // Skip initial emission
//                            awaitItem()
//                            awaitItem().trackAddedToPlaylist shouldBe null
//                            cancelAndIgnoreRemainingEvents()
//                        }
//                    }
//                }
//
//                "BackClicked pops back stack" {
//                    val subject = getSubject()
//
//                    subject.handle(TrackSearchUserAction.BackClicked)
//
//                    navController.backStack.isEmpty() shouldBe true
//                }
//            }
//    }) {
//        companion object {
//            private const val PLAYLIST_ID = 1L
//    }
