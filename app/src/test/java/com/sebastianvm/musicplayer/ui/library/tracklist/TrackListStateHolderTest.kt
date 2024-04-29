package com.sebastianvm.musicplayer.ui.library.tracklist

import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListState
import com.sebastianvm.musicplayer.features.track.list.TrackListStateHolder
import com.sebastianvm.musicplayer.features.track.list.toHeaderState
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FakeProvider
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

class TrackListStateHolderTest : FreeSpec({

    lateinit var trackRepository: FakeTrackRepositoryImpl
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    beforeTest {
        trackRepository = FakeProvider.trackRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    fun TestScope.getSubject(trackList: TrackList = MediaGroup.AllTracks): TrackListStateHolder {
        return TrackListStateHolder(
            stateHolderScope = this,
            trackRepository = trackRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            args = TrackListArguments(trackList),
        )
    }

    "init subscribes to changes in track list" - {
        withData(
            FixtureProvider.trackListWithMetadataFixtures().toList()
        ) { trackListWithMetadata ->
            val subject = getSubject()
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                trackRepository.getTrackListWithMetaDataValue.emit(
                    TrackListWithMetadata(
                        metaData = null,
                        trackList = listOf()
                    )
                )
                sortPreferencesRepository.getTrackListSortPreferencesValue.emit(
                    MediaSortPreferences(
                        sortOption = SortOptions.TrackListSortOptions.TRACK,
                        sortOrder = MediaSortOrder.ASCENDING
                    )
                )
                awaitItem() shouldBe Empty
                trackRepository.getTrackListWithMetaDataValue.emit(trackListWithMetadata)

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
            FixtureProvider.trackListSortPreferences().toList()
        ) { sortPreferences ->
            val subject = getSubject()
            val initialSortPreferences = MediaSortPreferences(
                sortOption = SortOptions.TrackListSortOptions.TRACK,
                sortOrder = MediaSortOrder.ASCENDING
            )
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                trackRepository.getTrackListWithMetaDataValue.emit(
                    FixtureProvider.trackListWithMetadataFixtures().toList().first()
                )
                sortPreferencesRepository.getTrackListSortPreferencesValue.emit(
                    initialSortPreferences
                )
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<TrackListState>>()
                    state.modelListState.sortButtonState shouldBe SortButtonState(
                        text = initialSortPreferences.sortOption.stringId,
                        sortOrder = initialSortPreferences.sortOrder
                    )
                }

                sortPreferencesRepository.getTrackListSortPreferencesValue.emit(sortPreferences)

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
        withData(FixtureProvider.trackListSortPreferences().toList()) { sortPreferences ->
            val subject = getSubject(trackList = MediaGroup.Album(albumId = 0))
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                trackRepository.getTrackListWithMetaDataValue.emit(
                    FixtureProvider.trackListWithMetadataFixtures().toList().first()
                )
                sortPreferencesRepository.getTrackListSortPreferencesValue.emit(
                    MediaSortPreferences(
                        sortOption = SortOptions.TrackListSortOptions.TRACK,
                        sortOrder = MediaSortOrder.ASCENDING
                    )
                )
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<TrackListState>>()
                    state.modelListState.sortButtonState shouldBe null
                }

                sortPreferencesRepository.getTrackListSortPreferencesValue.emit(sortPreferences)
                expectNoEvents()
            }
        }
    }
})
