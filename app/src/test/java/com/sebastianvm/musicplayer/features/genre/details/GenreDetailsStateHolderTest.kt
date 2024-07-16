package com.sebastianvm.musicplayer.features.genre.details

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.datatest.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.Genre
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class GenreDetailsStateHolderTest :
    FreeSpec({
        lateinit var genreRepositoryDep: FakeGenreRepository
        lateinit var sortPreferencesRepositoryDep:
            com.sebastianvm.musicplayer.core.datatest.preferences.FakeSortPreferencesRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            genreRepositoryDep = FakeGenreRepository()
            sortPreferencesRepositoryDep =
                com.sebastianvm.musicplayer.core.datatest.preferences
                    .FakeSortPreferencesRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            genre: Genre = FixtureProvider.genre(id = GENRE_ID, name = GENRE_NAME),
            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOption> =
                MediaSortPreferences(SortOptions.Track, MediaSortOrder.ASCENDING),
        ): GenreDetailsStateHolder {
            genreRepositoryDep.genres.value = listOf(genre)
            sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
                mapOf(genre.id to sortPreferences)
            navControllerDep.push(
                uiComponent =
                    GenreDetailsUiComponent(
                        arguments =
                            GenreDetailsArguments(genreId = genre.id, genreName = genre.name),
                        navController = navControllerDep,
                    ))

            return GenreDetailsStateHolder(
                stateHolderScope = this,
                genreRepository = genreRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                args = GenreDetailsArguments(genreId = genre.id, genreName = genre.name),
                navController = navControllerDep,
                playbackManager = playbackManagerDep,
            )
        }

        "init sets state and subscribes to changes in genre" {
            val genre = FixtureProvider.genre()

            val subject = getSubject(genre = genre)
            testStateHolderState(subject) {
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

                    testStateHolderState(subject) {
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
                        BackStackEntry(
                            uiComponent =
                                SortMenuUiComponent(
                                    arguments =
                                        SortMenuArguments(
                                            listType = SortableListType.Genre(GENRE_ID))),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
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
                            ))
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(
                        GenreDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackContextMenu(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.Genre(GENRE_ID),
                                        ),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
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
