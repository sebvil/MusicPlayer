package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TrackListStateHolderTest :
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

        fun TestScope.getSubject(): TrackListStateHolder {
            return TrackListStateHolder(
                stateHolderScope = this,
                trackRepository = trackRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                navController = navControllerDep,
                playbackManager = playbackManagerDep,
            )
        }

        fun updateSortPreferences(
            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
        ) {
            sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences
        }

        "init subscribes to changes in track list" {
            val subject = getSubject()
            trackRepositoryDep.tracks.value = emptyList()

            testStateHolderState(subject) {
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
                        MediaSortPreferences(
                            sortOption = SortOptions.TrackListSortOptions.TRACK,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                    val tracks = FixtureProvider.tracks()
                    trackRepositoryDep.tracks.value = tracks

                    updateSortPreferences(initialSortPreferences)

                    val subject = getSubject()

                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackListState.Loading

                        awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                text = initialSortPreferences.sortOption.stringId,
                                sortOrder = initialSortPreferences.sortOrder,
                            )

                        updateSortPreferences(sortPreferences)
                        if (sortPreferences == initialSortPreferences) {
                            expectNoEvents()
                        } else {
                            awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
                                SortButton.State(
                                    text = sortPreferences.sortOption.stringId,
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
                        BackStackEntry(
                            uiComponent =
                                SortMenuUiComponent(
                                    arguments =
                                        SortMenuArguments(listType = SortableListType.AllTracks)
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
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
                        BackStackEntry(
                            uiComponent =
                                TrackContextMenu(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.AllTracks,
                                        ),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
    }
}
