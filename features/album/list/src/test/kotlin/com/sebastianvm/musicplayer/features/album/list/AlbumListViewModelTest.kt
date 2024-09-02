package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.core.datatest.extensions.toAlbumWithArtists
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AlbumListViewModelTest :
    FreeSpec({
        lateinit var albumRepositoryDep: FakeAlbumRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            albumRepositoryDep = FakeAlbumRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): AlbumListViewModel {
            return AlbumListViewModel(
                vmScope = this,
                albumRepository = albumRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                navController = navControllerDep,
                features = initializeFakeFeatures(),
            )
        }

        "init subscribes to changes in album list" {
            val subject = getSubject()
            albumRepositoryDep.albums.value = emptyList()
            sortPreferencesRepositoryDep.albumListSortPreferences.value =
                MediaSortPreferences(
                    sortOption = SortOptions.Album,
                    sortOrder = MediaSortOrder.ASCENDING,
                )
            testViewModelState(subject) {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Empty
                val albums = FixtureProvider.albums()
                albumRepositoryDep.albums.value = albums
                val item = awaitItem().shouldBeInstanceOf<Data<AlbumListState>>()
                item.state.albums shouldBe
                    albums.map { AlbumRow.State.fromAlbum(it.toAlbumWithArtists()) }
            }
        }

        "init subscribes to changes in sort order" -
            {
                withData(FixtureProvider.albumSortPreferences().toList()) { sortPreferences ->
                    val subject = getSubject()
                    val initialPrefs =
                        MediaSortPreferences<SortOptions.AlbumListSortOption>(
                            sortOption = SortOptions.Album,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                    albumRepositoryDep.albums.value = FixtureProvider.albums().toList()
                    sortPreferencesRepositoryDep.albumListSortPreferences.value = initialPrefs

                    testViewModelState(subject) {
                        awaitItem() shouldBe Loading

                        with(awaitItem()) {
                            shouldBeInstanceOf<Data<AlbumListState>>()
                            state.sortButtonState shouldBe
                                SortButton.State(
                                    option = initialPrefs.sortOption,
                                    sortOrder = initialPrefs.sortOrder,
                                )
                        }

                        if (sortPreferences != initialPrefs) {
                            sortPreferencesRepositoryDep.albumListSortPreferences.value =
                                sortPreferences
                            with(awaitItem()) {
                                shouldBeInstanceOf<Data<AlbumListState>>()
                                state.sortButtonState shouldBe
                                    SortButton.State(
                                        option = sortPreferences.sortOption,
                                        sortOrder = sortPreferences.sortOrder,
                                    )
                            }
                        }
                    }
                }
            }

        "handle" -
            {
                "AlbumMoreIconClicked navigates to AlbumContextMenu" {
                    val subject = getSubject()
                    subject.handle(AlbumListUserAction.AlbumMoreIconClicked(ALBUM_ID))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments = AlbumContextMenuArguments(ALBUM_ID),
                                    name = "AlbumContextMenu",
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "SortButtonClicked navigates to SortMenu" {
                    val subject = getSubject()
                    subject.handle(AlbumListUserAction.SortButtonClicked)
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        SortMenuArguments(listType = SortableListType.Albums),
                                    name = "SortMenu",
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "AlbumClicked navigates to album details" {
                    val subject = getSubject()
                    subject.handle(
                        AlbumListUserAction.AlbumClicked(
                            albumItem =
                                AlbumRow.State(
                                    id = ALBUM_ID,
                                    albumName = ALBUM_NAME,
                                    artists = ARTIST_NAME,
                                    artworkUri = IMAGE_URI,
                                )
                        )
                    )

                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    name = "AlbumDetails",
                                    arguments =
                                        AlbumDetailsArguments(
                                            albumId = ALBUM_ID,
                                            albumName = ALBUM_NAME,
                                            imageUri = IMAGE_URI,
                                            artists = ARTIST_NAME,
                                        ),
                                ),
                            navOptions =
                                NavOptions(presentationMode = NavOptions.PresentationMode.Screen),
                        )
                }
            }
    }) {
    companion object {
        private const val ALBUM_ID = 1L
        private const val ALBUM_NAME = "Album 1"
        private const val IMAGE_URI = "imageUri"
        private const val ARTIST_NAME = "Artist 1"
    }
}
