package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
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
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.update

class TrackListStateHolderTest : FreeSpec({

    lateinit var trackRepositoryDep: FakeTrackRepository
    lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
    lateinit var playbackManagerDep: FakePlaybackManager

    beforeTest {
        trackRepositoryDep = FakeTrackRepository()
        sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
        playbackManagerDep = FakePlaybackManager()
    }

    fun TestScope.getSubject(trackList: TrackList = MediaGroup.AllTracks): TrackListStateHolder {
        return TrackListStateHolder(
            stateHolderScope = this,
            trackRepository = trackRepositoryDep,
            sortPreferencesRepository = sortPreferencesRepositoryDep,
            args = TrackListArguments(trackList),
            navController = FakeNavController(),
            playbackManager = playbackManagerDep
        )
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
            val subject = getSubject()
            val initialSortPreferences = MediaSortPreferences(
                sortOption = SortOptions.TrackListSortOptions.TRACK,
                sortOrder = MediaSortOrder.ASCENDING
            )
            trackRepositoryDep.trackListsWithMetadata.update {
                it + (
                    MediaGroup.AllTracks to FixtureProvider.trackListWithMetadataFixtures()
                        .first()
                    )
            }

            sortPreferencesRepositoryDep.allTracksSortPreferences.value = initialSortPreferences

            testStateHolderState(subject) {
                awaitItem() shouldBe Loading

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<TrackListState>>()
                    state.modelListState.sortButtonState shouldBe SortButtonState(
                        text = initialSortPreferences.sortOption.stringId,
                        sortOrder = initialSortPreferences.sortOrder
                    )
                }

                sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences

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
})
