package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.Playlist
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsProps
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class PlaylistDetailsViewModelTest :
    FreeSpec({
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
            playlistRepositoryDep = FakePlaylistRepository()
        }

        fun TestScope.getSubject(
            playlist: Playlist = FixtureProvider.playlist(id = PLAYLIST_ID),
            sortPreferences: MediaSortPreferences<SortOptions.PlaylistSortOption> =
                MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING),
        ): PlaylistDetailsViewModel {
            playlistRepositoryDep.playlists.value = listOf(playlist)

            sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                mapOf(playlist.id to sortPreferences)

            navControllerDep.push(FakeMvvmComponent())
            return PlaylistDetailsViewModel(
                viewModelScope = this,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                arguments =
                    PlaylistDetailsArguments(
                        playlistId = playlist.id,
                        playlistName = playlist.name,
                    ),
                props = MutableStateFlow(PlaylistDetailsProps(navController = navControllerDep)),
                playbackManager = playbackManagerDep,
                playlistRepository = playlistRepositoryDep,
                features = FakeFeatures(),
            )
        }

        "init subscribes to changes in playlistrack list" {
            val playlist = FixtureProvider.playlist()
            val subject = getSubject(playlist = playlist)
            testViewModelState(subject) {
                awaitItem() shouldBe PlaylistDetailsState.Loading(playlistName = playlist.name)
                with(awaitItemAs<PlaylistDetailsState.Data>()) {
                    tracks shouldBe playlist.tracks.map { TrackRow.State.fromTrack(it) }
                    playlistName shouldBe playlist.name
                }

                val updatedPlaylist = FixtureProvider.playlist(id = playlist.id)
                playlistRepositoryDep.playlists.value = listOf(updatedPlaylist)
                with(awaitItemAs<PlaylistDetailsState.Data>()) {
                    tracks shouldBe updatedPlaylist.tracks.map { TrackRow.State.fromTrack(it) }
                    playlistName shouldBe updatedPlaylist.name
                }
            }
        }

        "init subscribes to changes in sort order" {
            val initialSortPreferences =
                MediaSortPreferences<SortOptions.PlaylistSortOption>(
                    sortOption = SortOptions.Track,
                    sortOrder = MediaSortOrder.ASCENDING,
                )

            val subject = getSubject(sortPreferences = initialSortPreferences)
            testViewModelState(subject) {
                skipItems(1)
                val state = awaitItemAs<PlaylistDetailsState.Data>()
                state.sortButtonState shouldBe
                    SortButton.State(
                        option = initialSortPreferences.sortOption,
                        sortOrder = initialSortPreferences.sortOrder,
                    )

                sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                    mapOf(
                        PLAYLIST_ID to
                            initialSortPreferences.copy(sortOrder = MediaSortOrder.DESCENDING)
                    )
                awaitItemAs<PlaylistDetailsState.Data>().sortButtonState shouldBe
                    SortButton.State(
                        option = initialSortPreferences.sortOption,
                        sortOrder = MediaSortOrder.DESCENDING,
                    )
            }
        }

        "handle" -
            {
                "SortButtonClicked navigates to SortMenu" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.SortButtonClicked)
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        SortMenuArguments(
                                            listType =
                                                SortableListType.Playlist(playlistId = PLAYLIST_ID)
                                        )
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Playlist(PLAYLIST_ID),
                                initialTrackIndex = TRACK_INDEX,
                            )
                        )
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(
                        PlaylistDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX)
                    )
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.Playlist(PLAYLIST_ID),
                                        )
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "BackClicked navigates back" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }

                "AddTracksButtonClicked navigates to TrackSearch" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.AddTracksButtonClicked)
                    navControllerDep.backStack shouldHaveSize 2
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments = TrackSearchArguments(playlistId = PLAYLIST_ID)
                                ),
                            navOptions =
                                NavOptions(presentationMode = NavOptions.PresentationMode.Screen),
                        )
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
        private const val PLAYLIST_ID = 1L
    }
}
