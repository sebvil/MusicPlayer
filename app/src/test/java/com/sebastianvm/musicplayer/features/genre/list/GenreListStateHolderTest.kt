package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
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

class GenreListStateHolderTest : FreeSpec({

    lateinit var genreRepository: FakeGenreRepository
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepository

    beforeTest {
        genreRepository = FakeGenreRepository()
        sortPreferencesRepository = FakeSortPreferencesRepository()
    }

    fun TestScope.getSubject(): GenreListStateHolder {
        return GenreListStateHolder(
            stateHolderScope = this,
            genreRepository = genreRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            navController = FakeNavController()
        )
    }

    "init subscribes to changes in genre list" {
        val subject = getSubject()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            genreRepository.genres.value = emptyList()
            sortPreferencesRepository.genreListSortOrder.value = MediaSortOrder.ASCENDING
            awaitItem() shouldBe Empty

            val genres = FixtureProvider.genreFixtures().toList()
            genreRepository.genres.value = genres
            with(awaitItem()) {
                shouldBeInstanceOf<Data<GenreListState>>()
                state.modelListState.items shouldBe genres.map {
                    it.toModelListItemState()
                }
                state.modelListState.headerState shouldBe HeaderState.None
            }
        }
    }

    "init subscribes to changes in sort order" {
        val subject = getSubject()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            genreRepository.genres.value = FixtureProvider.genreFixtures().toList()
            sortPreferencesRepository.genreListSortOrder.value = MediaSortOrder.ASCENDING
            with(awaitItem()) {
                shouldBeInstanceOf<Data<GenreListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.genre_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            }

            sortPreferencesRepository.genreListSortOrder.value = MediaSortOrder.DESCENDING
            with(awaitItem()) {
                shouldBeInstanceOf<Data<GenreListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.genre_name,
                    sortOrder = MediaSortOrder.DESCENDING,
                )
            }
        }
    }

    "sortByButtonClicked toggles sort order" {
        val subject = getSubject()
        subject.handle(GenreListUserAction.SortByButtonClicked)
        advanceUntilIdle()
        sortPreferencesRepository.genreListSortOrder.value shouldBe MediaSortOrder.DESCENDING
    }
})
