package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.resources.RString
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ArtistListStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): ArtistListStateHolder {
            return ArtistListStateHolder(
                stateHolderScope = this,
                artistRepository = artistRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                navController = navControllerDep,
            )
        }

        "init subscribes to changes in artist list" {
            val subject = getSubject()
            artistRepositoryDep.artists.value = emptyList()
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Empty

                val artists = FixtureProvider.artists()
                artistRepositoryDep.artists.value = artists
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.artists shouldBe artists.map { ArtistRow.State.fromArtist(it) }
                }
            }
        }

        "init subscribes to changes in sort order" {
            val subject = getSubject()
            artistRepositoryDep.artists.value = FixtureProvider.artists()
            sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.ASCENDING
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading

                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.sortButtonState shouldBe
                        SortButton.State(
                            text = RString.artist_name,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                }

                sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.DESCENDING
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<ArtistListState>>()
                    state.sortButtonState shouldBe
                        SortButton.State(
                            text = RString.artist_name,
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
                        BackStackEntry(
                            uiComponent =
                                ArtistUiComponent(
                                    arguments = ArtistArguments(ARTIST_ID),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.Screen,
                        )
                }

                "ArtistMoreIconClicked navigates to ArtistContextMenu" {
                    val subject = getSubject()
                    subject.handle(ArtistListUserAction.ArtistMoreIconClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                ArtistContextMenu(
                                    arguments = ArtistContextMenuArguments(ARTIST_ID)
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }
            }
    }) {

    companion object {
        private const val ARTIST_ID = 1L
    }
}
