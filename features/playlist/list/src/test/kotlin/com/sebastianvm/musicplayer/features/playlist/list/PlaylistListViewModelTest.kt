package com.sebastianvm.musicplayer.features.playlist.list

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datatest.extensions.toBasicPlaylist
import com.sebastianvm.musicplayer.core.datatest.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.Playlist
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListProps
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class PlaylistListViewModelTest :
    FreeSpec({
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            playlistRepositoryDep = FakePlaylistRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            playlists: List<Playlist> = FixtureProvider.playlists(),
            sortOrder: MediaSortOrder = MediaSortOrder.ASCENDING,
            shouldPlaylistCreationFail: Boolean = false,
        ): PlaylistListViewModel {
            playlistRepositoryDep.playlists.value = playlists
            sortPreferencesRepositoryDep.playlistListSortOrder.value = sortOrder
            playlistRepositoryDep.shouldPlaylistCreationFail = shouldPlaylistCreationFail
            navControllerDep.push(FakeMvvmComponent())

            return PlaylistListViewModel(
                playlistRepository = playlistRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                props = MutableStateFlow(PlaylistListProps(navController = navControllerDep)),
                features = initializeFakeFeatures(),
                vmScope = this,
            )
        }

        "init subscribes to changes " -
            {
                "in playlist list" {
                    val subject = getSubject(playlists = emptyList())

                    testViewModelState(subject) {
                        awaitItem() shouldBe PlaylistListState.Loading
                        awaitItem() shouldBe
                            PlaylistListState.Empty(
                                isCreatePlaylistDialogOpen = false,
                                isPlaylistCreationErrorDialogOpen = false,
                            )

                        val playlists = FixtureProvider.playlists()
                        playlistRepositoryDep.playlists.value = playlists

                        awaitItemAs<PlaylistListState.Data>().playlists shouldBe
                            playlists.map { PlaylistRow.State.fromPlaylist(it.toBasicPlaylist()) }
                    }
                }

                "in sort order" {
                    val subject = getSubject(sortOrder = MediaSortOrder.ASCENDING)

                    testViewModelState(subject) {
                        awaitItem() shouldBe PlaylistListState.Loading
                        awaitItemAs<PlaylistListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = RString.playlist_name,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )

                        sortPreferencesRepositoryDep.playlistListSortOrder.value =
                            MediaSortOrder.DESCENDING
                        awaitItemAs<PlaylistListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = RString.playlist_name,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                    }
                }
            }

        "handle" -
            {
                "PlaylistMoreIconClicked navigates to PlaylistContextMenu" {
                    val subject = getSubject()

                    subject.handle(
                        PlaylistListUserAction.PlaylistMoreIconClicked(playlistId = PLAYLIST_ID)
                    )

                    navControllerDep.backStack shouldHaveSize 2
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            FakeMvvmComponent(
                                arguments = PlaylistContextMenuArguments(playlistId = PLAYLIST_ID)
                            ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "SortByClicked toggles sort order" {
                    val subject = getSubject(sortOrder = MediaSortOrder.ASCENDING)
                    testViewModelState(subject) {
                        skipItems(1)
                        awaitItemAs<PlaylistListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = RString.playlist_name,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                        subject.handle(PlaylistListUserAction.SortByClicked)
                        awaitItemAs<PlaylistListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = RString.playlist_name,
                                sortOrder = MediaSortOrder.DESCENDING,
                            )
                        subject.handle(PlaylistListUserAction.SortByClicked)
                        awaitItemAs<PlaylistListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = RString.playlist_name,
                                sortOrder = MediaSortOrder.ASCENDING,
                            )
                    }
                }

                "PlaylistClicked navigates to PlaylistDetails" {
                    val subject = getSubject()

                    subject.handle(
                        PlaylistListUserAction.PlaylistClicked(
                            playlistId = PLAYLIST_ID,
                            playlistName = PLAYLIST_NAME,
                        )
                    )

                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments = PlaylistDetailsArguments(PLAYLIST_ID, PLAYLIST_NAME)
                                ),
                            navOptions =
                                NavOptions(presentationMode = NavOptions.PresentationMode.Screen),
                        )
                }

                "CreatePlaylistButtonClicked" -
                    {
                        "navigates to PlaylistDetails on success" {
                            val subject = getSubject(shouldPlaylistCreationFail = false)

                            subject.handle(
                                PlaylistListUserAction.CreatePlaylistButtonClicked(
                                    playlistName = PLAYLIST_NAME
                                )
                            )
                            advanceUntilIdle()
                            val playlist = playlistRepositoryDep.playlists.value.last()
                            navControllerDep.backStack shouldHaveSize 2

                            navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    mvvmComponent =
                                        FakeMvvmComponent(
                                            arguments =
                                                PlaylistDetailsArguments(
                                                    playlistId = playlist.id,
                                                    playlistName = PLAYLIST_NAME,
                                                )
                                        ),
                                    navOptions =
                                        NavOptions(
                                            presentationMode = NavOptions.PresentationMode.Screen
                                        ),
                                )
                        }

                        "shows error dialog on failure and DismissPlaylistCreationErrorDialog closes the dialog" {
                            val subject = getSubject(shouldPlaylistCreationFail = true)

                            testViewModelState(subject) {
                                skipItems(2)
                                subject.handle(
                                    PlaylistListUserAction.CreatePlaylistButtonClicked(
                                        playlistName = PLAYLIST_NAME
                                    )
                                )

                                awaitItemAs<PlaylistListState.Data>()
                                    .isPlaylistCreationErrorDialogOpen shouldBe true

                                subject.handle(
                                    PlaylistListUserAction.DismissPlaylistCreationErrorDialog
                                )
                                awaitItemAs<PlaylistListState.Data>()
                                    .isPlaylistCreationErrorDialogOpen shouldBe false
                            }
                        }
                    }

                "CreateNewPlaylistButtonClicked opens create playlist dialog and DismissPlaylistCreationDialog dismisses it " {
                    val subject = getSubject()

                    testViewModelState(subject) {
                        skipItems(2)
                        subject.handle(PlaylistListUserAction.CreateNewPlaylistButtonClicked)
                        awaitItemAs<PlaylistListState.Data>().isCreatePlaylistDialogOpen shouldBe
                            true
                        subject.handle(PlaylistListUserAction.DismissPlaylistCreationDialog)
                        awaitItemAs<PlaylistListState.Data>().isCreatePlaylistDialogOpen shouldBe
                            false
                    }
                }
            }
    }) {
    companion object {
        private const val PLAYLIST_ID = 1L
        private const val PLAYLIST_NAME = "Playlist"
    }
}
