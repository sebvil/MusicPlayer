package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datatest.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListProps
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.MutableStateFlow

class ArtistListViewModelTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): ArtistListViewModel {
            return ArtistListViewModel(
                viewModelScope = this,
                artistRepository = artistRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                props = MutableStateFlow(ArtistListProps(navController = navControllerDep)),
                features = FakeFeatures(),
            )
        }

        "init subscribes to changes in artist list" {
            val subject = getSubject()
            artistRepositoryDep.artists.value = emptyList()
            testViewModelState(subject) {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Empty

                val artists = FixtureProvider.artists()
                artistRepositoryDep.artists.value = artists
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.artists shouldBe
                        artists.map {
                            com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
                                .State
                                .fromArtist(it)
                        }
                }
            }
        }

        "init subscribes to changes in sort order" {
            val subject = getSubject()
            artistRepositoryDep.artists.value = FixtureProvider.artists()
            sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.ASCENDING
            testViewModelState(subject) {
                awaitItem() shouldBe Loading

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.sortButtonState shouldBe
                        com.sebastianvm.musicplayer.core.designsystems.components.SortButton.State(
                            text = com.sebastianvm.musicplayer.core.resources.RString.artist_name,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                }

                sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.DESCENDING
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.sortButtonState shouldBe
                        com.sebastianvm.musicplayer.core.designsystems.components.SortButton.State(
                            text = com.sebastianvm.musicplayer.core.resources.RString.artist_name,
                            sortOrder = MediaSortOrder.DESCENDING,
                        )
                }
            }
        }

        "handle" -
            {
                "SortByButtonClicked toggles sort order" {
                    val subject = getSubject()
                    subject.handle(ArtistListUserAction.SortByButtonClicked)
                    advanceUntilIdle()
                    sortPreferencesRepositoryDep.artistListSortOrder.value shouldBe
                        MediaSortOrder.DESCENDING
                }

                "ArtistClicked navigates to ArtistScreen" {
                    val subject = getSubject()
                    subject.handle(ArtistListUserAction.ArtistClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(arguments = ArtistDetailsArguments(ARTIST_ID)),
                            navOptions =
                                NavOptions(presentationMode = NavOptions.PresentationMode.Screen),
                        )
                }

                "ArtistMoreIconClicked navigates to ArtistContextMenu" {
                    val subject = getSubject()
                    subject.handle(ArtistListUserAction.ArtistMoreIconClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments = ArtistContextMenuArguments(ARTIST_ID)
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }
            }
    }) {

    companion object {
        private const val ARTIST_ID = 1L
    }
}
