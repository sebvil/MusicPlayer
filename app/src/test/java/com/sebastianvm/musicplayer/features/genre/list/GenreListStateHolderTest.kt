package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.designsystem.components.GenreRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
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

class GenreListStateHolderTest :
    FreeSpec({
        lateinit var genreRepositoryDep: FakeGenreRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            genreRepositoryDep = FakeGenreRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): GenreListStateHolder {
            return GenreListStateHolder(
                stateHolderScope = this,
                genreRepository = genreRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                navController = navControllerDep,
            )
        }

        "init subscribes to changes in genre list" {
            val subject = getSubject()
            genreRepositoryDep.genres.value = emptyList()
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                awaitItem() shouldBe Empty

                val genres = FixtureProvider.genres()
                genreRepositoryDep.genres.value = genres
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<GenreListState>>()
                    state.genres shouldBe genres.map { GenreRow.State.fromGenre(it) }
                }
            }
        }

        "init subscribes to changes in sort order" {
            val subject = getSubject()
            genreRepositoryDep.genres.value = FixtureProvider.genres()
            sortPreferencesRepositoryDep.genreListSortOrder.value = MediaSortOrder.ASCENDING
            testStateHolderState(subject) {
                awaitItem() shouldBe Loading
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<GenreListState>>()
                    state.sortButtonState shouldBe
                        SortButton.State(
                            text = RString.genre_name,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )
                }

                sortPreferencesRepositoryDep.genreListSortOrder.value = MediaSortOrder.DESCENDING
                with(awaitItem()) {
                    shouldBeInstanceOf<Data<GenreListState>>()
                    state.sortButtonState shouldBe
                        SortButton.State(
                            text = RString.genre_name,
                            sortOrder = MediaSortOrder.DESCENDING,
                        )
                }
            }
        }

        "handle" -
            {
                "SortByButtonClicked toggles sort order" {
                    val subject = getSubject()
                    subject.handle(GenreListUserAction.SortByButtonClicked)
                    advanceUntilIdle()
                    sortPreferencesRepositoryDep.genreListSortOrder.value shouldBe
                        MediaSortOrder.DESCENDING
                }

                "GenreClicked navigates to TrackList" {
                    val subject = getSubject()
                    subject.handle(GenreListUserAction.GenreClicked(GENRE_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackListUiComponent(
                                    arguments =
                                        TrackListArguments(
                                            trackListType = MediaGroup.Genre(GENRE_ID)
                                        ),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.Screen,
                        )
                }

                "GenreMoreIconClicked navigates to GenreContextMenu" {
                    val subject = getSubject()
                    subject.handle(GenreListUserAction.GenreMoreIconClicked(GENRE_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                GenreContextMenu(arguments = GenreContextMenuArguments(GENRE_ID)),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }
            }
    }) {

    companion object {
        private const val GENRE_ID = 1L
    }
}
