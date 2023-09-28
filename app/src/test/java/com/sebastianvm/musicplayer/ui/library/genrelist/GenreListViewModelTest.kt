package com.sebastianvm.musicplayer.ui.library.genrelist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
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

class GenreListViewModelTest : BaseTest() {
    private lateinit var genreRepository: FakeGenreRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        genreRepository = FakeProvider.genreRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(): GenreListViewModel {
        return GenreListViewModel(
            initialState = GenreListState(
                modelListState = ModelListState(
                    items = listOf(),
                    sortButtonState = null,
                    headerState = HeaderState.None
                ),
                isLoading = true
            ),
            viewModelScope = testScope,
            genreRepository = genreRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init subscribes to changes in track list`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(state.isLoading).isTrue()

                genreRepository.getGenresValue.emit(listOf())
                Truth.assertThat(state.modelListState.items).isEmpty()
                Truth.assertThat(state.modelListState.headerState).isEqualTo(HeaderState.None)
                Truth.assertThat(state.isLoading).isFalse()

                val genres = FixtureProvider.genreFixtures().toList()
                genreRepository.getGenresValue.emit(genres)
                Truth.assertThat(state.modelListState.items)
                    .isEqualTo(genres.map { it.toModelListItemState() })
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
            sortPreferencesRepository.getGenreListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
                SortButtonState(
                    text = R.string.genre_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getGenreListSortOrderValue.emit(sortOrder)
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
                SortButtonState(
                    text = R.string.genre_name,
                    sortOrder = sortOrder
                )
            )
        }
    }

    @Test
    fun `SortByButtonClicked toggles sort order`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                handle(GenreListUserAction.SortByButtonClicked)
                Truth.assertThat(sortPreferencesRepository.toggleGenreListSortOrderInvocations)
                    .containsExactly(
                        listOf<Any>()
                    )
            }
        }
}
