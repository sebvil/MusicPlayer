package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ArtistListStateHolderTest : FreeSpec({

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
            navController = navControllerDep
        )
    }

    "init subscribes to changes in artist list" {
        val subject = getSubject()
        artistRepositoryDep.artists.value = emptyList()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe Empty

            val artists = FixtureProvider.artistFixtures().toList()
            artistRepositoryDep.artists.value = artists
            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.items shouldBe artists.map {
                    it.toModelListItemState(
                        trailingButtonType = TrailingButtonType.More
                    )
                }
                state.modelListState.headerState shouldBe HeaderState.None
            }
        }
    }

    "init subscribes to changes in sort order" {
        val subject = getSubject()
        artistRepositoryDep.artists.value = FixtureProvider.artistFixtures().toList()
        sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.ASCENDING
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading

            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            }

            sortPreferencesRepositoryDep.artistListSortOrder.value = MediaSortOrder.DESCENDING
            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.DESCENDING,
                )
            }
        }
    }

    "handle" - {

        "SortByButtonClicked toggles sort order" {
            val subject = getSubject()
            subject.handle(ArtistListUserAction.SortByButtonClicked)
            advanceUntilIdle()
            sortPreferencesRepositoryDep.artistListSortOrder.value shouldBe MediaSortOrder.DESCENDING
        }

        "ArtistClicked navigates to ArtistScreen" {
            val subject = getSubject()
            subject.handle(ArtistListUserAction.ArtistClicked(ARTIST_ID))
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                uiComponent = ArtistUiComponent(
                    arguments = ArtistArguments(ARTIST_ID),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.Screen
            )
        }

        "ArtistMoreIconClicked navigates to ArtistContextMenu" {
            val subject = getSubject()
            subject.handle(ArtistListUserAction.ArtistMoreIconClicked(ARTIST_ID))
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                uiComponent = ArtistContextMenu(
                    arguments = ArtistContextMenuArguments(ARTIST_ID)
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet
            )
        }
    }
}) {

    companion object {
        private const val ARTIST_ID = 1L
    }
}
