package com.sebastianvm.musicplayer.ui.library.artistlist

import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.features.artist.list.ArtistListState
import com.sebastianvm.musicplayer.features.artist.list.ArtistListStateHolder
import com.sebastianvm.musicplayer.features.artist.list.ArtistListUserAction
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepositoryImpl
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.SortButtonState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
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

class ArtistListStateHolderTest : FreeSpec({

    lateinit var artistRepository: FakeArtistRepositoryImpl
    lateinit var sortPreferencesRepository: FakeSortPreferencesRepositoryImpl

    beforeTest {
        artistRepository = FakeProvider.artistRepository
        sortPreferencesRepository = FakeProvider.sortPreferencesRepository
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
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            artistRepository.getArtistsValue.emit(emptyList())
            sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            awaitItem() shouldBe Empty

            val artists = FixtureProvider.artistFixtures().toList()
            artistRepository.getArtistsValue.emit(artists)
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
        testStateHolderState(subject) {
            awaitItem() shouldBe Loading
            artistRepository.getArtistsValue.emit(FixtureProvider.artistFixtures().toList())
            sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.ASCENDING)
            with(awaitItem()) {
                shouldBeInstanceOf<Data<ArtistListState>>()
                state.modelListState.sortButtonState shouldBe SortButtonState(
                    text = R.string.artist_name,
                    sortOrder = MediaSortOrder.ASCENDING
                )
            }

            sortPreferencesRepository.getArtistListSortOrderValue.emit(MediaSortOrder.DESCENDING)
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
        sortPreferencesRepository.toggleArtistListSortOrderInvocations shouldBe listOf(listOf())
    }
})
