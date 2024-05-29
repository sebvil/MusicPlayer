package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
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

    lateinit var artistRepository: FakeArtistRepository
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepository

    beforeTest {
        artistRepository = FakeArtistRepository()
        sortPreferencesRepository = FakeSortPreferencesRepository()
    }

    fun TestScope.getSubject(): ArtistListStateHolder {
        return ArtistListStateHolder(
            stateHolderScope = this,
            artistRepository = artistRepository,
            sortPreferencesRepository = sortPreferencesRepository,
            navController = FakeNavController()
        )
    }

    "init subscribes to changes in artist list" {
        val subject = getSubject()
        artistRepository.artists.value = emptyList()
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            awaitItem() shouldBe Empty

            val artists = FixtureProvider.artistFixtures().toList()
            artistRepository.artists.value = artists
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
        artistRepository.artists.value = FixtureProvider.artistFixtures().toList()
        sortPreferencesRepository.artistListSortOrder.value = MediaSortOrder.ASCENDING
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading

            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            }

            sortPreferencesRepository.artistListSortOrder.value = MediaSortOrder.DESCENDING
            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.DESCENDING,
                )
            }
        }
    }

    "sortByButtonClicked toggles sort order" {
        val subject = getSubject()
        subject.handle(ArtistListUserAction.SortByButtonClicked)
        advanceUntilIdle()
        sortPreferencesRepository.artistListSortOrder.value shouldBe MediaSortOrder.DESCENDING
    }
})
