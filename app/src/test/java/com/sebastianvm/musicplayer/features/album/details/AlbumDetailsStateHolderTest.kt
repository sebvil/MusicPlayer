// package com.sebastianvm.musicplayer.features.album.details
//
// import com.sebastianvm.musicplayer.database.entities.GenreTrackCrossRef
// import com.sebastianvm.musicplayer.designsystem.components.SortButton
// import com.sebastianvm.musicplayer.designsystem.components.TrackRow
// import com.sebastianvm.musicplayer.designsystem.icons.Album
// import com.sebastianvm.musicplayer.designsystem.icons.Icons
// import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
// import com.sebastianvm.musicplayer.features.navigation.FakeNavController
// import com.sebastianvm.musicplayer.features.navigation.NavOptions
// import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
// import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
// import com.sebastianvm.musicplayer.features.sort.SortableListType
// import com.sebastianvm.musicplayer.features.track.list.TrackListState
// import com.sebastianvm.musicplayer.features.track.list.TrackListStateHolder
// import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
// import com.sebastianvm.musicplayer.features.track.list.TrackListUserAction
// import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
// import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
// import com.sebastianvm.musicplayer.player.MediaGroup
// import com.sebastianvm.musicplayer.player.TrackList
// import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
// import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
// import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
// import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
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
// class AlbumDetailsStateHolderTest :
//    FreeSpec({
//        lateinit var trackRepositoryDep: FakeTrackRepository
//        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
//        lateinit var playbackManagerDep: FakePlaybackManager
//        lateinit var navControllerDep: FakeNavController
//
//        beforeTest {
//            trackRepositoryDep = FakeTrackRepository()
//            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
//            playbackManagerDep = FakePlaybackManager()
//            navControllerDep = FakeNavController()
//        }
//
//        fun TestScope.getSubject(
//            trackList: TrackList = MediaGroup.AllTracks
//        ): TrackListStateHolder {
//            return TrackListStateHolder(
//                stateHolderScope = this,
//                trackRepository = trackRepositoryDep,
//                sortPreferencesRepository = sortPreferencesRepositoryDep,
//                args = TrackListArguments(trackList),
//                navController = navControllerDep,
//                playbackManager = playbackManagerDep,
//            )
//        }
//
//        fun updateSortPreferences(
//            trackList: TrackList,
//            sortPreferences: MediaSortPreferences<SortOptions.TrackListSortOptions>,
//        ) {
//            when (trackList) {
//                is MediaGroup.Album,
//                is MediaGroup.Playlist -> Unit
//                MediaGroup.AllTracks -> {
//                    sortPreferencesRepositoryDep.allTracksSortPreferences.value = sortPreferences
//                }
//                is MediaGroup.Genre -> {
//                    sortPreferencesRepositoryDep.genreTracksSortPreferences.value =
//                        mapOf(trackList.genreId to sortPreferences)
//                }
//            }
//        }
//
//        "init subscribes to changes in track list" -
//            {
//                "for all tracks" {
//                    val subject = getSubject()
//                    trackRepositoryDep.tracks.value = emptyList()
//
//                    testStateHolderState(subject) {
//                        awaitItem() shouldBe TrackListState.Loading
//                        awaitItemAs<TrackListState.Data>().tracks.shouldBeEmpty()
//
//                        val tracks = FixtureProvider.tracks()
//                        trackRepositoryDep.tracks.value = tracks
//
//                        val state = awaitItemAs<TrackListState.Data>()
//                        state.tracks shouldBe tracks.map { TrackRow.State.fromTrack(it) }
//                        state.headerState shouldBe Header.State.None
//                    }
//                }
//
//                "for album" {
//                    val album = FixtureProvider.album(id = ALBUM_ID)
//                    val tracksInAlbum = List(10) { FixtureProvider.track(albumId = ALBUM_ID) }
//                    val tracksNotInAlbum =
//                        List(10) { FixtureProvider.track(albumId = ALBUM_ID + 1) }
//                    trackRepositoryDep.tracks.value = tracksInAlbum + tracksNotInAlbum
//                    trackRepositoryDep.albums.value = listOf(album)
//
//                    val subject = getSubject(trackList = MediaGroup.Album(albumId = ALBUM_ID))
//                    testStateHolderState(subject) {
//                        awaitItem() shouldBe TrackListState.Loading
//
//                        val state = awaitItemAs<TrackListState.Data>()
//                        state.tracks shouldBe tracksInAlbum.map { TrackRow.State.fromTrack(it) }
//                        state.headerState shouldBe
//                            Header.State.WithImage(
//                                title = album.title,
//                                imageState =
//                                    MediaArtImageState(
//                                        imageUri = album.imageUri,
//                                        backupImage = Icons.Album,
//                                    ),
//                            )
//                    }
//                }
//
//                "for genre" {
//                    val genre = FixtureProvider.genre(id = GENRE_ID)
//                    val size = 10
//                    val tracks = FixtureProvider.tracks(size = size)
//                    val tracksInGenre = tracks.take(size / 2)
//                    trackRepositoryDep.tracks.value = tracks
//                    trackRepositoryDep.genres.value = listOf(genre)
//                    trackRepositoryDep.genreTrackCrossRefs.value =
//                        tracksInGenre.map {
//                            GenreTrackCrossRef(genreId = genre.id, trackId = it.id)
//                        }
//
//                    val subject = getSubject(trackList = MediaGroup.Genre(genreId = GENRE_ID))
//                    testStateHolderState(subject) {
//                        awaitItem() shouldBe TrackListState.Loading
//
//                        val state = awaitItemAs<TrackListState.Data>()
//                        state.tracks shouldBe tracksInGenre.map { TrackRow.State.fromTrack(it) }
//                        state.headerState shouldBe Header.State.Simple(title = genre.name)
//                    }
//                }
//            }
//
//        "init subscribes to changes in sort order" -
//            {
//                withData(nameFn = { it.toString() }, FixtureProvider.trackListSortPreferences()) {
//                    sortPreferences ->
//                    withData(listOf(MediaGroup.AllTracks, MediaGroup.Genre(genreId = 0))) {
//                        trackListType ->
//                        val initialSortPreferences =
//                            MediaSortPreferences(
//                                sortOption = SortOptions.TrackListSortOptions.TRACK,
//                                sortOrder = MediaSortOrder.ASCENDING,
//                            )
//                        val tracks = FixtureProvider.tracks()
//                        trackRepositoryDep.tracks.value = tracks
//                        when (trackListType) {
//                            is MediaGroup.Genre -> {
//                                trackRepositoryDep.genres.value =
//                                    listOf(FixtureProvider.genre(id = trackListType.genreId))
//                                trackRepositoryDep.genreTrackCrossRefs.value =
//                                    tracks.map {
//                                        GenreTrackCrossRef(
//                                            genreId = trackListType.genreId,
//                                            trackId = it.id,
//                                        )
//                                    }
//                            }
//                            else -> Unit
//                        }
//
//                        updateSortPreferences(trackListType, initialSortPreferences)
//
//                        val subject = getSubject(trackList = trackListType)
//
//                        testStateHolderState(subject) {
//                            awaitItem() shouldBe TrackListState.Loading
//
//                            awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
//                                SortButton.State(
//                                    text = initialSortPreferences.sortOption.stringId,
//                                    sortOrder = initialSortPreferences.sortOrder,
//                                )
//
//                            updateSortPreferences(trackListType, sortPreferences)
//                            if (sortPreferences == initialSortPreferences) {
//                                expectNoEvents()
//                            } else {
//                                awaitItemAs<TrackListState.Data>().sortButtonState shouldBe
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
//                withData(FixtureProvider.trackListSortPreferences()) { sortPreferences ->
//                    val subject = getSubject(trackList = MediaGroup.Album(albumId = ALBUM_ID))
//                    trackRepositoryDep.tracks.value = FixtureProvider.tracks(albumId = ALBUM_ID)
//                    trackRepositoryDep.albums.value = listOf(FixtureProvider.album(id = ALBUM_ID))
//
//                    testStateHolderState(subject) {
//                        awaitItem() shouldBe TrackListState.Loading
//
//                        awaitItemAs<TrackListState.Data>().sortButtonState shouldBe null
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
//                    subject.handle(TrackListUserAction.SortButtonClicked)
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
//                    subject.handle(TrackListUserAction.TrackClicked(TRACK_INDEX))
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
//                    subject.handle(TrackListUserAction.TrackMoreIconClicked(TRACK_ID,
// TRACK_INDEX))
//                    navControllerDep.backStack.last() shouldBe
//                        BackStackEntry(
//                            uiComponent =
//                                TrackContextMenu(
//                                    arguments =
//                                        TrackContextMenuArguments(
//                                            trackId = TRACK_ID,
//                                            trackPositionInList = TRACK_INDEX,
//                                            trackList = MediaGroup.AllTracks,
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
//                            TrackListUiComponent(
//                                arguments = TrackListArguments(MediaGroup.AllTracks),
//                                navController = navControllerDep,
//                            )
//                    )
//                    val subject = getSubject()
//                    subject.handle(TrackListUserAction.BackClicked)
//                    navControllerDep.backStack.shouldBeEmpty()
//                }
//            }
//    }) {
//    companion object {
//        private const val TRACK_ID = 1L
//        private const val TRACK_INDEX = 0
//        private const val ALBUM_ID = 1L
//        private const val GENRE_ID = 1L
//    }
// }
