package com.sebastianvm.musicplayer.ui.library.albumlist

import com.google.common.truth.Truth
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
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
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class AlbumListStateHolderTest : BaseTest() {

    private lateinit var albumRepository: FakeAlbumRepositoryImpl
    private lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    @BeforeEach
    fun beforeEach() {
        albumRepository = FakeProvider.albumRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    private fun generateViewModel(): AlbumListStateHolder {
        return AlbumListStateHolder(
            stateHolderScope = testScope,
            albumRepository = albumRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    @Test
    fun `init subscribes to changes in track list`() =
        testScope.runSafeTest {
            with(generateViewModel()) {
                Truth.assertThat(currentState).isEqualTo(Loading)

                albumRepository.getAlbumsValue.emit(listOf())
                sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(
                    MediaSortPreferences(
                        sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                        sortOrder = MediaSortOrder.ASCENDING
                    )
                )
                Truth.assertThat(currentState).isEqualTo(Empty)

                val albums = FixtureProvider.albumFixtures().toList()
                albumRepository.getAlbumsValue.emit(albums)
                Truth.assertThat(
                    getDataState().modelListState.items
                )
                    .isEqualTo(albums.map { it.toModelListItemState() })
                Truth.assertThat(
                    getDataState().modelListState.headerState
                ).isEqualTo(HeaderState.None)
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
            albumRepository.getAlbumsValue.emit(FixtureProvider.albumFixtures().toList())
            Truth.assertThat(
                getDataState().modelListState.sortButtonState
            ).isEqualTo(
                SortButtonState(
                    text = SortOptions.AlbumListSortOptions.ALBUM.stringId,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(sortPreferences)
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
}
