package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.datatest.track.FakeTrackRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.track.list.TrackListProps
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class TrackListViewModelTest :
    FreeSpec({
        lateinit var trackRepositoryDep: FakeTrackRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            trackRepositoryDep = FakeTrackRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): TrackListViewModel {
            return TrackListViewModel(
                vmScope = this,
                trackRepository = trackRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                props = MutableStateFlow(TrackListProps(navController = navControllerDep)),
                features = FakeFeatures(),
                playbackManager = playbackManagerDep,
            )
        }

        fun updateSortPreferences(
            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOption>
        ) {
            sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences
        }

        "init subscribes to changes in track list" {
            val subject = getSubject()
            trackRepositoryDep.tracks.value = emptyList()

            testViewModelState(subject) {
                awaitItem() shouldBe TrackListState.Loading
                awaitItemAs<TrackListState.Data>().tracks.shouldBeEmpty()

                val tracks = FixtureProvider.tracks()
                trackRepositoryDep.tracks.value = tracks

                val state = awaitItemAs<TrackListState.Data>()
                state.tracks shouldBe tracks.map { TrackRow.State.fromTrack(it) }
            }
        }

        "init subscribes to changes in sort order" -
            {
                withData(nameFn = { it.toString() }, FixtureProvider.trackListSortPreferences()) {
                    sortPreferences ->
                    val initialSortPreferences =
                        MediaSortPreferences<SortOptions.TrackListSortOption>(
                            sortOption = SortOptions.Track,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                    val tracks = FixtureProvider.tracks()
                    trackRepositoryDep.tracks.value = tracks

                    updateSortPreferences(initialSortPreferences)

                    val subject = getSubject()

                    testViewModelState(subject) {
                        awaitItem() shouldBe TrackListState.Loading

                        awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                option = initialSortPreferences.sortOption,
                                sortOrder = initialSortPreferences.sortOrder,
                            )

                        updateSortPreferences(sortPreferences)
                        if (sortPreferences == initialSortPreferences) {
                            expectNoEvents()
                        } else {
                            awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
                                SortButton.State(
                                    option = sortPreferences.sortOption,
                                    sortOrder = sortPreferences.sortOrder,
                                )
                        }
                    }
                }
            }

        "handle" -
            {
                "SortButtonClicked navigates to SortMenu" {
                    val subject = getSubject()
                    subject.handle(TrackListUserAction.SortButtonClicked)
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        SortMenuArguments(listType = SortableListType.AllTracks)
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(TrackListUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.AllTracks,
                                initialTrackIndex = TRACK_INDEX,
                            )
                        )
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(TrackListUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.AllTracks,
                                        )
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
    }
}
