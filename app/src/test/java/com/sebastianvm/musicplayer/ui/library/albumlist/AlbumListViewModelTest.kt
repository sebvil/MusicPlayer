package com.sebastianvm.musicplayer.ui.library.albumlist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepositoryImpl
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
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class AlbumListViewModelTest : BaseTest() {

    private lateinit var albumRepository: FakeAlbumRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        albumRepository = FakeProvider.albumRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(): AlbumListViewModel {
        return AlbumListViewModel(
            initialState = AlbumListState(
                modelListState = ModelListState(
                    items = listOf(),
                    sortButtonState = null,
                    headerState = HeaderState.None
                ),
                isLoading = true
            ),
            viewModelScope = testScope,
            albumRepository = albumRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init subscribes to changes in track list`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(state.isLoading).isTrue()

                albumRepository.getAlbumsValue.emit(listOf())
                Truth.assertThat(state.modelListState.items).isEmpty()
                Truth.assertThat(state.modelListState.headerState).isEqualTo(HeaderState.None)
                Truth.assertThat(state.isLoading).isFalse()

                val albums = FixtureProvider.albumFixtures().toList()
                albumRepository.getAlbumsValue.emit(albums)
                Truth.assertThat(state.modelListState.items)
                    .isEqualTo(albums.map { it.toModelListItemState() })
                Truth.assertThat(state.modelListState.headerState).isEqualTo(HeaderState.None)
                Truth.assertThat(state.isLoading).isFalse()
            }
        }

    @ParameterizedTest
    @MethodSource("com.sebastianvm.musicplayer.util.FixtureProvider#albumSortPreferences")
    fun `init subscribes to changes in sort order`(
        sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>
    ) = testScope.runSafeTest {
        with(generateViewModel()) {
            sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
                SortButtonState(
                    text = SortOptions.AlbumListSortOptions.ALBUM.stringId,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(sortPreferences)
            Truth.assertThat(state.modelListState.sortButtonState).isEqualTo(
                SortButtonState(
                    text = sortPreferences.sortOption.stringId,
                    sortOrder = sortPreferences.sortOrder
                )
            )
        }
    }
}
