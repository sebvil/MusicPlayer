package com.sebastianvm.musicplayer.ui.library.artistlist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.BaseTest
import com.sebastianvm.musicplayer.util.FakeProvider
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.currentState
import com.sebastianvm.musicplayer.util.getDataState
import com.sebastianvm.musicplayer.util.runSafeTest
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class ArtistListStateHolderTest : BaseTest() {
    private lateinit var artistRepository: FakeArtistRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        artistRepository = FakeProvider.artistRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(): ArtistListStateHolder {
        return ArtistListStateHolder(
            stateHolderScope = testScope,
            artistRepository = artistRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init subscribes to changes in track list`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(currentState).isEqualTo(Loading)

                artistRepository.getArtistsValue.emit(listOf())
                sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.ASCENDING)
                Truth.assertThat(currentState).isEqualTo(Empty)

                val artists = FixtureProvider.artistFixtures().toList()
                artistRepository.getArtistsValue.emit(artists)
                Truth.assertThat(
                    getDataState().modelListState.items
                )
                    .isEqualTo(artists.map { it.toModelListItemState(trailingButtonType = TrailingButtonType.More) })
                Truth.assertThat(
                    getDataState().modelListState.headerState
                ).isEqualTo(HeaderState.None)
            }
        }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#sortOrders")
    fun `init subscribes to changes in sort order`(
        sortOrder: MediaSortOrder
    ) = testScope.runSafeTest {
        with(generateViewModel()) {
            sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            artistRepository.getArtistsValue.emit(FixtureProvider.artistFixtures().toList())
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isEqualTo(
                SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getArtistListSortOrderValue.emit(sortOrder)
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isEqualTo(
                SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = sortOrder
                )
            )
        }
    }

    @Test
    fun `SortByButtonClicked toggles sort order`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                handle(ArtistListUserAction.SortByButtonClicked)
                Truth.assertThat(sortPreferencesRepository.toggleArtistListSortOrderInvocations)
                    .containsExactly(
                        listOf<Any>()
                    )
            }
        }
}
