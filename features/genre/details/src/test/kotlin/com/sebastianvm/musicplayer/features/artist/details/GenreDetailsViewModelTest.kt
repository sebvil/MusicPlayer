package com.sebastianvm.musicplayer.features.artist.details

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.Genre
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsProps
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsState
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsUserAction
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsViewModel
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class GenreDetailsViewModelTest :
    FreeSpec({
        lateinit var genreRepositoryDep: FakeGenreRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            genreRepositoryDep = FakeGenreRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            genre: Genre = FixtureProvider.genre(id = GENRE_ID, name = GENRE_NAME),
            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOption> =
                MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING),
        ): GenreDetailsViewModel {
            genreRepositoryDep.genres.value = listOf(genre)
            sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                mapOf(genre.id to sortPreferences)
            navControllerDep.push(FakeMvvmComponent())

            return GenreDetailsViewModel(
                viewModelScope = this,
                genreRepository = genreRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                arguments = GenreDetailsArguments(genreId = genre.id, genreName = genre.name),
                props = MutableStateFlow(GenreDetailsProps(navController = navControllerDep)),
                playbackManager = playbackManagerDep,
                features = FakeFeatures(),
            )
        }

        "init sets state and subscribes to changes in genre" {
            val genre = FixtureProvider.genre()

            val subject = getSubject(genre = genre)
            testViewModelState(subject) {
                awaitItem() shouldBe GenreDetailsState.Loading(genreName = genre.name)
                with(awaitItemAs<GenreDetailsState.Data>()) {
                    tracks shouldBe genre.tracks.map { TrackRow.State.fromTrack(it) }
                    genreName shouldBe genre.name
                }

                val updatedGenre = FixtureProvider.genre(id = genre.id)
                genreRepositoryDep.genres.value = listOf(updatedGenre)
                with(awaitItemAs<GenreDetailsState.Data>()) {
                    tracks shouldBe updatedGenre.tracks.map { TrackRow.State.fromTrack(it) }
                    genreName shouldBe updatedGenre.name
                }
            }
        }

        "init subscribes to changes in sort order" -
            {
                withData(nameFn = { it.toString() }, FixtureProvider.trackListSortPreferences()) {
                    sortPreferences ->
                    val initialSortPreferences =
                        MediaSortPreferences<SortOptions.TrackListSortOption>(
                            sortOption = SortOptions.Track,
                            sortOrder = MediaSortOrder.ASCENDING,
                        )

                    val subject = getSubject(sortPreferences = initialSortPreferences)

                    testViewModelState(subject) {
                        awaitItem() shouldBe GenreDetailsState.Loading(genreName = GENRE_NAME)

                        awaitItemAs<GenreDetailsState.Data>().sortButtonState shouldBe
                            SortButton.State(
                                option = initialSortPreferences.sortOption,
                                sortOrder = initialSortPreferences.sortOrder,
                            )

                        sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                            mapOf(GENRE_ID to sortPreferences)
                        if (sortPreferences == initialSortPreferences) {
                            expectNoEvents()
                        } else {
                            awaitItemAs<GenreDetailsState.Data>().sortButtonState shouldBe
                                SortButton.State(
                                    option = sortPreferences.sortOption,
                                    sortOrder = sortPreferences.sortOrder,
                                )
                        }
                    }
                }
            }

        "handle" -
            {
                "SortButtonClicked navigates to SortMenu" {
                    val subject = getSubject()
                    subject.handle(GenreDetailsUserAction.SortButtonClicked)
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        SortMenuArguments(
                                            listType = SortableListType.Genre(GENRE_ID)
                                        )
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(GenreDetailsUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Genre(GENRE_ID),
                                initialTrackIndex = TRACK_INDEX,
                            )
                        )
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(
                        GenreDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX)
                    )
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.Genre(GENRE_ID),
                                        )
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "BackClicked navigates back" {
                    val subject = getSubject()
                    subject.handle(GenreDetailsUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
        private const val GENRE_ID = 1L
        private const val GENRE_NAME = "Genre"
    }
}
