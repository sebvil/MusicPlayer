package com.sebastianvm.musicplayer.ui.library.genrelist

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FakeProvider
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GenreListStateHolderTest : FreeSpec({

    lateinit var genreRepository: FakeGenreRepositoryImpl
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    beforeTest {
        genreRepository = FakeProvider.genreRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
    }

    fun TestScope.getSubject(): GenreListStateHolder {
        return GenreListStateHolder(
            stateHolderScope = this,
            genreRepository = genreRepository,
            sortPreferencesRepository = sortPreferencesRepository
        )
    }

    "init subscribes to changes in genre list" {
        val subject = getSubject()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            genreRepository.getGenresValue.emit(emptyList())
            sortPreferencesRepository.getGenreListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            awaitItem() shouldBe Empty

            val genres = FixtureProvider.genreFixtures().toList()
            genreRepository.getGenresValue.emit(genres)
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
            genreRepository.getGenresValue.emit(FixtureProvider.genreFixtures().toList())
            sortPreferencesRepository.getGenreListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            with(awaitItem()) {
                shouldBeInstanceOf<Data<GenreListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.genre_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            }

            sortPreferencesRepository.getGenreListSortOrderValue.emit(MediaSortOrder.DESCENDING)
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
        sortPreferencesRepository.toggleGenreListSortOrderInvocations shouldBe listOf(listOf())
    }
})
