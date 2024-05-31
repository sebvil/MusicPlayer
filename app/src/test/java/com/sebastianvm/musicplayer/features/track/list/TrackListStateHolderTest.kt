package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.update

class TrackListStateHolderTest : FreeSpec({

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

    fun TestScope.getSubject(trackList: TrackList = MediaGroup.AllTracks): TrackListStateHolder {
        return TrackListStateHolder(
            stateHolderScope = this,
            trackRepository = trackRepositoryDep,
            sortPreferencesRepository = sortPreferencesRepositoryDep,
            args = TrackListArguments(trackList),
            navController = navControllerDep,
            playbackManager = playbackManagerDep
        )
    }

    fun updateSortPreferences(
        trackList: TrackList,
        sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) {
        when (trackList) {
            is MediaGroup.Album -> Unit
            MediaGroup.AllTracks -> {
                sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences
            }

            is MediaGroup.Genre -> {
                sortPreferencesRepositoryDep.genreTracksSortPreferences.value = mapOf(
                    trackList.genreId to sortPreferences
                )
            }

            is MediaGroup.Playlist -> {
                sortPreferencesRepositoryDep.playlistTracksSortPreferences.value = mapOf(
                    trackList.playlistId to sortPreferences
                )
            }
        }
    }

    "init subscribes to changes in track list" - {
        withData(
            FixtureProvider.trackListWithMetadataFixtures()
        ) { trackListWithMetadata ->
            val subject = getSubject()
            trackRepositoryDep.trackListsWithMetadata.update {
                it + (
                    MediaGroup.AllTracks to TrackListWithMetadata(
                        metaData = null,
                        trackList = listOf()
                    )
                    )
            }
            sortPreferencesRepositoryDep.allTracksSortPreferences.value = MediaSortPreferences(
                sortOption = SortOptions.TrackListSortOptions.TRACK,
                sortOrder = MediaSortOrder.ASCENDING
            )
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading

                awaitItem() shouldBe Empty
                trackRepositoryDep.trackListsWithMetadata.update { it + (MediaGroup.AllTracks to trackListWithMetadata) }

                if (trackListWithMetadata.trackList.isEmpty()) {
                    expectNoEvents()
                } else {
                    with(awaitItem()) {
                        shouldBeInstanceOf<Data<TrackListState>>()
                        state.modelListState.items shouldBe trackListWithMetadata.trackList.map { it.toModelListItemState() }
                        state.modelListState.headerState shouldBe trackListWithMetadata.metaData.toHeaderState()
                    }
                }
            }
        }
    }

    "init subscribes to changes in sort order" - {
        withData(
            FixtureProvider.trackListSortPreferences()
        ) { sortPreferences ->
            withData(
                listOf(
                    MediaGroup.AllTracks,
                    MediaGroup.Genre(genreId = 0),
                    MediaGroup.Playlist(playlistId = 0)
                )
            ) { trackListType ->
                val subject = getSubject(trackList = trackListType)
                val initialSortPreferences = MediaSortPreferences(
                    sortOption = SortOptions.TrackListSortOptions.TRACK,
                    sortOrder = MediaSortOrder.ASCENDING
                )
                trackRepositoryDep.trackListsWithMetadata.update {
                    it + (
                        trackListType to FixtureProvider.trackListWithMetadataFixtures()
                            .first()
                        )
                }

                updateSortPreferences(trackListType, initialSortPreferences)

                testStateHolderState(subject) {
                    awaitItem() shouldBe Loading

                    with(awaitItem()) {
                        shouldBeInstanceOf<Data<TrackListState>>()
                        state.modelListState.sortButtonState shouldBe SortButtonState(
                            text = initialSortPreferences.sortOption.stringId,
                            sortOrder = initialSortPreferences.sortOrder
                        )
                    }
                    updateSortPreferences(trackListType, sortPreferences)
                    if (sortPreferences == initialSortPreferences) {
                        expectNoEvents()
                    } else {
                        with(awaitItem()) {
                            shouldBeInstanceOf<Data<TrackListState>>()
                            state.modelListState.sortButtonState shouldBe SortButtonState(
                                text = sortPreferences.sortOption.stringId,
                                sortOrder = sortPreferences.sortOrder
                            )
                        }
                    }
                }
            }
        }
    }

    "init does not subscribe to changes in sort order for album" - {
        withData(FixtureProvider.trackListSortPreferences()) { sortPreferences ->
            val subject = getSubject(trackList = MediaGroup.Album(albumId = 0))
            trackRepositoryDep.trackListsWithMetadata.update {
                it + (
                    MediaGroup.Album(albumId = 0) to FixtureProvider.trackListWithMetadataFixtures()
                        .first()
                    )
            }

            testStateHolderState(subject) {
                awaitItem() shouldBe Loading

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<TrackListState>>()
                    state.modelListState.sortButtonState shouldBe null
                }

                sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences
                expectNoEvents()
            }
        }
    }

    "handle" - {
        "SortButtonClicked navigates to SortMenu" {
            val subject = getSubject()
            subject.handle(TrackListUserAction.SortButtonClicked)
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                screen = SortMenu(
                    arguments = SortMenuArguments(listType = SortableListType.Tracks(trackList = MediaGroup.AllTracks))
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet,
            )
        }

        "TrackClicked plays media" {
            val subject = getSubject()
            subject.handle(TrackListUserAction.TrackClicked(TRACK_INDEX))
            advanceUntilIdle()
            playbackManagerDep.playMediaInvocations shouldBe listOf(
                FakePlaybackManager.PlayMediaArguments(
                    mediaGroup = MediaGroup.AllTracks,
                    initialTrackIndex = TRACK_INDEX
                )
            )
        }

        "TrackMoreIconClicked navigates to TrackContextMenu" {
            val subject = getSubject()
            subject.handle(TrackListUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX))
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                screen = TrackContextMenu(
                    arguments = TrackContextMenuArguments(
                        trackId = TRACK_ID,
                        trackPositionInList = TRACK_INDEX,
                        trackList = MediaGroup.AllTracks
                    ),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet
            )
        }

        "BackClicked navigates back" {
            navControllerDep.push(
                screen = TrackList(
                    arguments = TrackListArguments(MediaGroup.AllTracks),
                    navController = navControllerDep
                )
            )
            val subject = getSubject()
            subject.handle(TrackListUserAction.BackClicked)
            navControllerDep.backStack.shouldBeEmpty()
        }
    }
}) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
    }
}
