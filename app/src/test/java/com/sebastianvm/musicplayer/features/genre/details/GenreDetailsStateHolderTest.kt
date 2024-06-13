// package com.sebastianvm.musicplayer.features.genre.details
//
// import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
// import com.sebastianvm.musicplayer.designsystem.components.SortButton
// import com.sebastianvm.musicplayer.designsystem.components.TrackRow
// import com.sebastianvm.musicplayer.designsystem.icons.Album
// import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
// import com.sebastianvm.musicplayer.features.navigation.FakeNavController
// import com.sebastianvm.musicplayer.features.navigation.NavOptions
// import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
// import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
// import com.sebastianvm.musicplayer.features.sort.SortableListType
// import com.sebastianvm.musicplayer.features.track.list.GenreDetailsState
// import com.sebastianvm.musicplayer.features.track.list.GenreDetailsStateHolder
// import com.sebastianvm.musicplayer.features.track.list.GenreDetailsUiComponent
// import com.sebastianvm.musicplayer.features.track.list.GenreDetailsUserAction
// import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
// import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
// import com.sebastianvm.musicplayer.player.MediaGroup
// import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
// import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
// import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
// import com.sebastianvm.musicplayer.util.FixtureProvider
// import com.sebastianvm.musicplayer.util.advanceUntilIdle
// import com.sebastianvm.musicplayer.util.awaitItemAs
// import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
// import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
// import com.sebastianvm.musicplayer.util.sort.SortOptions
// import com.sebastianvm.musicplayer.util.testStateHolderState
// import io.kotest.core.spec.style.FreeSpec
// import io.kotest.core.test.TestScope
// import io.kotest.datatest.withData
// import io.kotest.matchers.collections.shouldBeEmpty
// import io.kotest.matchers.shouldBe
//
// class GenreDetailsStateHolderTest :
//    FreeSpec({
//        lateinit var genreRepositoryDep: FakeGenreRepository
//        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
//        lateinit var playbackManagerDep: FakePlaybackManager
//        lateinit var navControllerDep: FakeNavController
//
//        beforeTest {
//            genreRepositoryDep = FakeGenreRepository()
//            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
//            playbackManagerDep = FakePlaybackManager()
//            navControllerDep = FakeNavController()
//        }
//
//        fun TestScope.getSubject(): GenreDetailsStateHolder {
//            return GenreDetailsStateHolder(
//                stateHolderScope = this,
//                genreRepository = genreRepositoryDep,
//                sortPreferencesRepository = sortPreferencesRepositoryDep,
//                args = GenreDetailsArguments(genreId = GENRE_ID, genreName = GENRE_NAME),
//                navController = navControllerDep,
//                playbackManager = playbackManagerDep,
//            )
//        }
//
//        fun updateSortPreferences(
//            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>
//        ) {
//            sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
//                mapOf(GENRE_ID to sortPreferences)
//        }
//
//        "init subscribes to changes in track list" {
//            val genre = FixtureProvider.genre(id = GENRE_ID)
//            val size = 10
//            val tracks = FixtureProvider.tracks(size = size)
//            val tracksInGenre = tracks.take(size / 2)
//            trackRepositoryDep.tracks.value = tracks
//            trackRepositoryDep.genres.value = listOf(genre)
//            trackRepositoryDep.genreTrackCrossRefs.value =
//                tracksInGenre.map { GenreTrackCrossRef(genreId = genre.id, trackId = it.id) }
//
//            val subject = getSubject(genreDetails = MediaGroup.Genre(genreId = GENRE_ID))
//            testStateHolderState(subject) {
//                awaitItem() shouldBe GenreDetailsState.Loading
//
//                val state = awaitItemAs<GenreDetailsState.Data>()
//                state.tracks shouldBe tracksInGenre.map { TrackRow.State.fromTrack(it) }
//                state.headerState shouldBe Header.State.Simple(title = genre.name)
//            }
//        }
//
//        "init subscribes to changes in sort order" -
//            {
//                withData(
//                    nameFn = { it.toString() },
//                    FixtureProvider.genreDetailsSortPreferences()
//                ) { sortPreferences ->
//                    withData(listOf(MediaGroup.AllTracks, MediaGroup.Genre(genreId = 0))) {
//                        genreDetailsType ->
//                        val initialSortPreferences =
//                            MediaSortPreferences(
//                                sortOption = SortOptions.GenreDetailsSortOptions.TRACK,
//                                sortOrder = MediaSortOrder.ASCENDING,
//                            )
//                        val tracks = FixtureProvider.tracks()
//                        trackRepositoryDep.tracks.value = tracks
//                        when (genreDetailsType) {
//                            is MediaGroup.Genre -> {
//                                trackRepositoryDep.genres.value =
//                                    listOf(FixtureProvider.genre(id = genreDetailsType.genreId))
//                                trackRepositoryDep.genreTrackCrossRefs.value =
//                                    tracks.map {
//                                        GenreTrackCrossRef(
//                                            genreId = genreDetailsType.genreId,
//                                            trackId = it.id,
//                                        )
//                                    }
//                            }
//                            else -> Unit
//                        }
//
//                        updateSortPreferences(genreDetailsType, initialSortPreferences)
//
//                        val subject = getSubject(genreDetails = genreDetailsType)
//
//                        testStateHolderState(subject) {
//                            awaitItem() shouldBe GenreDetailsState.Loading
//
//                            awaitItemAs<GenreDetailsState.Data>().sortButtonState shouldBe
//                                SortButton.State(
//                                    text = initialSortPreferences.sortOption.stringId,
//                                    sortOrder = initialSortPreferences.sortOrder,
//                                )
//
//                            updateSortPreferences(genreDetailsType, sortPreferences)
//                            if (sortPreferences == initialSortPreferences) {
//                                expectNoEvents()
//                            } else {
//                                awaitItemAs<GenreDetailsState.Data>().sortButtonState shouldBe
//                                    SortButton.State(
//                                        text = sortPreferences.sortOption.stringId,
//                                        sortOrder = sortPreferences.sortOrder,
//                                    )
//                            }
//                        }
//                    }
//                }
//            }
//
//        "init does not subscribe to changes in sort order for album" -
//            {
//                withData(FixtureProvider.genreDetailsSortPreferences()) { sortPreferences ->
//                    val subject = getSubject(genreDetails = MediaGroup.Album(albumId = ALBUM_ID))
//                    trackRepositoryDep.tracks.value = FixtureProvider.tracks(albumId = ALBUM_ID)
//                    trackRepositoryDep.albums.value = listOf(FixtureProvider.album(id = ALBUM_ID))
//
//                    testStateHolderState(subject) {
//                        awaitItem() shouldBe GenreDetailsState.Loading
//
//                        awaitItemAs<GenreDetailsState.Data>().sortButtonState shouldBe null
//
//                        sortPreferencesRepositoryDep.allTracksSortPreferences.value =
//                            sortPreferences
//                        expectNoEvents()
//                    }
//                }
//            }
//
//        "handle" -
//            {
//                "SortButtonClicked navigates to SortMenu" {
//                    val subject = getSubject()
//                    subject.handle(GenreDetailsUserAction.SortButtonClicked)
//                    navControllerDep.backStack.last() shouldBe
//                        BackStackEntry(
//                            uiComponent =
//                                SortMenuUiComponent(
//                                    arguments =
//                                        SortMenuArguments(listType = SortableListType.AllTracks)
//                                ),
//                            presentationMode = NavOptions.PresentationMode.BottomSheet,
//                        )
//                }
//
//                "TrackClicked plays media" {
//                    val subject = getSubject()
//                    subject.handle(GenreDetailsUserAction.TrackClicked(TRACK_INDEX))
//                    advanceUntilIdle()
//                    playbackManagerDep.playMediaInvocations shouldBe
//                        listOf(
//                            FakePlaybackManager.PlayMediaArguments(
//                                mediaGroup = MediaGroup.AllTracks,
//                                initialTrackIndex = TRACK_INDEX,
//                            )
//                        )
//                }
//
//                "TrackMoreIconClicked navigates to TrackContextMenu" {
//                    val subject = getSubject()
//                    subject.handle(
//                        GenreDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX)
//                    )
//                    navControllerDep.backStack.last() shouldBe
//                        BackStackEntry(
//                            uiComponent =
//                                TrackContextMenu(
//                                    arguments =
//                                        TrackContextMenuArguments(
//                                            trackId = TRACK_ID,
//                                            trackPositionInList = TRACK_INDEX,
//                                            genreDetails = MediaGroup.AllTracks,
//                                        ),
//                                    navController = navControllerDep,
//                                ),
//                            presentationMode = NavOptions.PresentationMode.BottomSheet,
//                        )
//                }
//
//                "BackClicked navigates back" {
//                    navControllerDep.push(
//                        uiComponent =
//                            GenreDetailsUiComponent(
//                                arguments = GenreDetailsArguments(MediaGroup.AllTracks),
//                                navController = navControllerDep,
//                            )
//                    )
//                    val subject = getSubject()
//                    subject.handle(GenreDetailsUserAction.BackClicked)
//                    navControllerDep.backStack.shouldBeEmpty()
//                }
//            }
//    }) {
//    companion object {
//        private const val TRACK_ID = 1L
//        private const val TRACK_INDEX = 0
//        private const val GENRE_ID = 1L
//        private const val GENRE_NAME = "Genre"
//    }
// }
