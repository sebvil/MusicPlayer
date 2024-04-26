package com.sebastianvm.musicplayer.ui.library.tracklist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.database.entities.TrackListWithMetadata
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.BaseTest
import com.sebastianvm.musicplayer.util.FakeProvider
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.getDataState
import com.sebastianvm.musicplayer.util.runSafeTest
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class TrackListStateHolderTest : BaseTest() {

    private lateinit var trackRepository: FakeTrackRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        trackRepository = FakeProvider.trackRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(trackList: TrackList = MediaGroup.AllTracks): TrackListStateHolder {
        return TrackListStateHolder(
            stateHolderScope = testScope,
            trackRepository = trackRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            args = TrackListArguments(trackList),
        )
    }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#trackListWithMetadataFixtures")
    fun `init subscribes to changes in track list`(trackListWithMetadata: TrackListWithMetadata) =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(state.value).isEqualTo(Loading)
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
                Truth.assertThat(state.value).isEqualTo(Empty)
                trackRepository.getTrackListWithMetaDataValue.emit(trackListWithMetadata)
                if (trackListWithMetadata.trackList.isEmpty()) {
                    Truth.assertThat(state.value).isEqualTo(Empty)
                } else {
                    Truth.assertThat(
                        getDataState().modelListState.items
                    ).isEqualTo(trackListWithMetadata.trackList.map { it.toModelListItemState() })
                    Truth.assertThat(
                        getDataState().modelListState.headerState
                    ).isEqualTo(trackListWithMetadata.metaData.toHeaderState())
                }
            }
        }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#trackListSortPreferences")
    fun `init subscribes to changes in sort order`(
        sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) = testScope.runSafeTest {
        with(generateViewModel()) {
            sortPreferencesRepository.getTrackListSortPreferencesValue.emit(
                MediaSortPreferences(
                    sortOption = SortOptions.TrackListSortOptions.TRACK,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            trackRepository.getTrackListWithMetaDataValue.emit(
                FixtureProvider.trackListWithMetadataFixtures().toList().first()
            )
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isEqualTo(
                SortButtonState(
                    text = SortOptions.TrackListSortOptions.TRACK.stringId,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getTrackListSortPreferencesValue.emit(sortPreferences)
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isEqualTo(
                SortButtonState(
                    text = sortPreferences.sortOption.stringId,
                    sortOrder = sortPreferences.sortOrder
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#trackListSortPreferences")
    fun `init does not subscribe to changes in sort order for album`(
        sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
    ) = testScope.runSafeTest {
        with(generateViewModel(trackList = MediaGroup.Album(albumId = 0))) {
            sortPreferencesRepository.getTrackListSortPreferencesValue.emit(
                MediaSortPreferences(
                    sortOption = SortOptions.TrackListSortOptions.TRACK,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            trackRepository.getTrackListWithMetaDataValue.emit(
                FixtureProvider.trackListWithMetadataFixtures().toList().first()
            )
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isNull()
            sortPreferencesRepository.getTrackListSortPreferencesValue.emit(sortPreferences)
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isNull()
        }
    }
}
