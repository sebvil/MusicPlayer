package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenu
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
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

class AlbumListStateHolderTest : FreeSpec({

    lateinit var albumRepositoryDep: FakeAlbumRepository
    lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
    lateinit var navControllerDep: FakeNavController

    beforeTest {
        albumRepositoryDep = FakeAlbumRepository()
        sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
        navControllerDep = FakeNavController()
    }

    fun TestScope.getSubject(): AlbumListStateHolder {
        return AlbumListStateHolder(
            stateHolderScope = this,
            albumRepository = albumRepositoryDep,
            sortPreferencesRepository = sortPreferencesRepositoryDep,
            navController = navControllerDep
        )
    }

    "init subscribes to changes in track list" {
        val subject = getSubject()
        albumRepositoryDep.albums.value = emptyList()
        sortPreferencesRepositoryDep.albumListSortPreferences.value =
            MediaSortPreferences(
                sortOption = SortOptions.AlbumListSortOptions.ALBUM,
                sortOrder = MediaSortOrder.ASCENDING
            )
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe Empty
            val albums = FixtureProvider.albumFixtures().toList()
            albumRepositoryDep.albums.value = albums
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
            albumRepositoryDep.albums.value = FixtureProvider.albumFixtures().toList()
            sortPreferencesRepositoryDep.albumListSortPreferences.value = initialPrefs

            testStateHolderState(subject) {
                awaitItem() shouldBe Loading

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<AlbumListState>>()
                    state.modelListState.sortButtonState shouldBe SortButtonState(
                        text = initialPrefs.sortOption.stringId,
                        sortOrder = initialPrefs.sortOrder
                    )
                }

                if (sortPreferences != initialPrefs) {
                    sortPreferencesRepositoryDep.albumListSortPreferences.value = sortPreferences
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

    "handle" - {
        "AlbumMoreIconClicked navigates to AlbumContextMenu" {
            val subject = getSubject()
            subject.handle(AlbumListUserAction.AlbumMoreIconClicked(ALBUM_ID))
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                uiComponent = AlbumContextMenu(
                    arguments = AlbumContextMenuArguments(ALBUM_ID),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet
            )
        }

        "SortButtonClicked navigates to SortMenu" {
            val subject = getSubject()
            subject.handle(AlbumListUserAction.SortButtonClicked)
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                uiComponent = SortMenu(
                    arguments = SortMenuArguments(listType = SortableListType.Albums)
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet
            )
        }

        "AlbumClicked navigates to TrackList" {
            val subject = getSubject()
            subject.handle(AlbumListUserAction.AlbumClicked(ALBUM_ID))

            navControllerDep.backStack.last() shouldBe BackStackEntry(
                uiComponent = TrackListUiComponent(
                    arguments = TrackListArguments(MediaGroup.Album(ALBUM_ID)),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.Screen
            )
        }
    }
}) {
    companion object {
        private const val ALBUM_ID = 1L
    }
}
