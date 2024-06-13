package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.playlist.tracksearch.TrackSearchUiComponent
import com.sebastianvm.musicplayer.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.sort.SortMenuUiComponent
import com.sebastianvm.musicplayer.features.sort.SortableListType
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.model.Track
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.FakeSortPreferencesRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PlaylistDetailsStateHolderTest :
    FreeSpec({
        lateinit var trackRepositoryDep: FakeTrackRepository
        lateinit var sortPreferencesRepositoryDep: FakeSortPreferencesRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            trackRepositoryDep = FakeTrackRepository()
            sortPreferencesRepositoryDep = FakeSortPreferencesRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
            playlistRepositoryDep = FakePlaylistRepository()
        }

        fun TestScope.getSubject(
            playlistId: Long = PLAYLIST_ID,
            tracks: List<Track> = FixtureProvider.tracks(),
            sortPreferences: MediaSortPreferences<SortOptions.PlaylistSortOptions> =
                MediaSortPreferences(
                    SortOptions.PlaylistSortOptions.TRACK,
                    MediaSortOrder.ASCENDING,
                ),
        ): PlaylistDetailsStateHolder {
            val playlist = FixtureProvider.playlist(id = PLAYLIST_ID, name = PLAYLIST_NAME)
            playlistRepositoryDep.playlists.value = listOf(playlist)
            trackRepositoryDep.tracks.value = tracks
            trackRepositoryDep.playlists.value = listOf(playlist)
            trackRepositoryDep.playlistTrackCrossRefs.value =
                tracks.mapIndexed { index, track ->
                    PlaylistTrackCrossRef(
                        playlistId = playlist.id,
                        trackId = track.id,
                        position = index.toLong(),
                    )
                }

            sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                mapOf(playlistId to sortPreferences)

            navControllerDep.push(
                uiComponent =
                    PlaylistDetailsUiComponent(
                        arguments =
                            PlaylistDetailsArguments(
                                playlistId = PLAYLIST_ID,
                                playlistName = PLAYLIST_NAME,
                            ),
                        navController = navControllerDep,
                    )
            )

            return PlaylistDetailsStateHolder(
                stateHolderScope = this,
                trackRepository = trackRepositoryDep,
                sortPreferencesRepository = sortPreferencesRepositoryDep,
                args =
                    PlaylistDetailsArguments(playlistId = playlistId, playlistName = PLAYLIST_NAME),
                navController = navControllerDep,
                playbackManager = playbackManagerDep,
                playlistRepository = playlistRepositoryDep,
            )
        }

        "init subscribes to changes in playlist track list" {
            val tracks = FixtureProvider.tracks()
            val subject = getSubject(playlistId = PLAYLIST_ID, tracks = tracks)
            testStateHolderState(subject) {
                awaitItem() shouldBe PlaylistDetailsState.Loading(playlistName = PLAYLIST_NAME)
                val state = awaitItemAs<PlaylistDetailsState.Data>()
                state.tracks shouldBe tracks.map { TrackRow.State.fromTrack(it) }
                state.playlistName shouldBe PLAYLIST_NAME
            }
        }

        "init subscribes to changes in sort order" {
            val initialSortPreferences =
                MediaSortPreferences(
                    sortOption = SortOptions.PlaylistSortOptions.TRACK,
                    sortOrder = MediaSortOrder.ASCENDING,
                )

            val subject = getSubject(sortPreferences = initialSortPreferences)
            testStateHolderState(subject) {
                skipItems(1)
                val state = awaitItemAs<PlaylistDetailsState.Data>()
                state.sortButtonState shouldBe
                    SortButton.State(
                        text = initialSortPreferences.sortOption.stringId,
                        sortOrder = initialSortPreferences.sortOrder,
                    )

                sortPreferencesRepositoryDep.playlistTracksSortPreferences.value =
                    mapOf(
                        PLAYLIST_ID to
                            initialSortPreferences.copy(sortOrder = MediaSortOrder.DESCENDING)
                    )
                awaitItemAs<PlaylistDetailsState.Data>().sortButtonState shouldBe
                    SortButton.State(
                        text = initialSortPreferences.sortOption.stringId,
                        sortOrder = MediaSortOrder.DESCENDING,
                    )
            }
        }

        "handle" -
            {
                "SortButtonClicked navigates to SortMenu" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.SortButtonClicked)
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                SortMenuUiComponent(
                                    arguments =
                                        SortMenuArguments(
                                            listType =
                                                SortableListType.Playlist(playlistId = PLAYLIST_ID)
                                        )
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }

                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Playlist(PLAYLIST_ID),
                                initialTrackIndex = TRACK_INDEX,
                            )
                        )
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(
                        PlaylistDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX)
                    )
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackContextMenu(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.Playlist(PLAYLIST_ID),
                                        ),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }

                "BackClicked navigates back" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }

                "AddTracksButtonClicked navigates to TrackSearch" {
                    val subject = getSubject()
                    subject.handle(PlaylistDetailsUserAction.AddTracksButtonClicked)
                    navControllerDep.backStack shouldHaveSize 2
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackSearchUiComponent(
                                    arguments = TrackSearchArguments(playlistId = PLAYLIST_ID),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.Screen,
                        )
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
        private const val PLAYLIST_ID = 1L
        private const val PLAYLIST_NAME = "Playlist"
    }
}
