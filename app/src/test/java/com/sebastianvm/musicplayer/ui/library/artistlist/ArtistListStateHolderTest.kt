package com.sebastianvm.musicplayer.ui.library.artistlist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.util.BaseTest
import com.sebastianvm.musicplayer.util.FakeProvider
import com.sebastianvm.musicplayer.util.FixtureProvider
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
            initialState = ArtistListState(
                modelListState = ModelListState(
                    items = listOf(),
                    sortButtonState = null,
                    headerState = HeaderState.None
                ),
                isLoading = true
            ),
            viewModelScope = testScope,
            artistRepository = artistRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init subscribes to changes in track list`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(state.isLoading).isTrue()

                artistRepository.getArtistsValue.emit(listOf())
                Truth.assertThat(state.modelListState.items).isEmpty()
                Truth.assertThat(state.modelListState.headerState).isEqualTo(HeaderState.None)
                Truth.assertThat(state.isLoading).isFalse()

                val artists = FixtureProvider.artistFixtures().toList()
                artistRepository.getArtistsValue.emit(artists)
                Truth.assertThat(state.modelListState.items)
                    .isEqualTo(artists.map { it.toModelListItemState(trailingButtonType = TrailingButtonType.More) })
                Truth.assertThat(state.modelListState.headerState).isEqualTo(HeaderState.None)
                Truth.assertThat(state.isLoading).isFalse()
            }
        }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#sortOrders")
    fun `init subscribes to changes in sort order`(
        sortOrder: MediaSortOrder
    ) = testScope.runSafeTest {
        with(generateViewModel()) {
            sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
                SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getArtistListSortOrderValue.emit(sortOrder)
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
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
