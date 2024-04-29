package com.sebastianvm.musicplayer.ui.library.albumlist

import com.sebastianvm.musicplayer.features.album.list.AlbumListState
import com.sebastianvm.musicplayer.features.album.list.AlbumListStateHolder
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
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

class AlbumListStateHolderTest : FreeSpec({

    lateinit var albumRepository: FakeAlbumRepositoryImpl
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    beforeTest {
        albumRepository = FakeProvider.albumRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    fun TestScope.getSubject(): AlbumListStateHolder {
        return AlbumListStateHolder(
            stateHolderScope = this,
            albumRepository = albumRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    "init subscribes to changes in track list" {
        val subject = getSubject()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            albumRepository.getAlbumsValue.emit(listOf())
            sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(
                MediaSortPreferences(
                    sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            )
            awaitItem() shouldBe Empty
            val albums = FixtureProvider.albumFixtures().toList()
            albumRepository.getAlbumsValue.emit(albums)
            val item = awaitItem().shouldBeInstanceOf<Data<AlbumListState>>()
            item.state.modelListState.items shouldBe albums.map { it.toModelListItemState() }
            item.state.modelListState.headerState shouldBe HeaderState.None
        }
    }

    "init subscribes to changes in sort order" - {
        withData(FixtureProvider.albumSortPreferences().toList()) { sortPreferences ->
            val subject = getSubject()
            val initialPrefs = MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.ASCENDING
            )
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                albumRepository.getAlbumsValue.emit(FixtureProvider.albumFixtures().toList())
                sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(initialPrefs)

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<AlbumListState>>()
                    state.modelListState.sortButtonState shouldBe SortButtonState(
                        text = initialPrefs.sortOption.stringId,
                        sortOrder = initialPrefs.sortOrder
                    )
                }

                if (sortPreferences != initialPrefs) {
                    sortPreferencesRepository.getAlbumListSortPreferencesValue.emit(sortPreferences)
                    with(awaitItem()) {
                        shouldBeInstanceOf<Data<AlbumListState>>()
                        state.modelListState.sortButtonState shouldBe SortButtonState(
                            text = sortPreferences.sortOption.stringId,
                            sortOrder = sortPreferences.sortOrder
                        )
                    }
                }
            }
        }
    }
})
