package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.repository.queue.FakeQueueRepository
import com.sebastianvm.musicplayer.repository.track.FakeTrackRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TrackContextMenuStateHolderTest :
    FreeSpec({
        lateinit var trackRepositoryDep: FakeTrackRepository
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var queueRepositoryDep: FakeQueueRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            trackRepositoryDep = FakeTrackRepository()
            playlistRepositoryDep = FakePlaylistRepository()
            queueRepositoryDep = FakeQueueRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            arguments: TrackContextMenuArguments =
                TrackContextMenuArguments(
                    trackId = TRACK_ID,
                    trackPositionInList = TRACK_POSITION_IN_LIST,
                    trackList = MediaGroup.AllTracks,
                )
        ): TrackContextMenuStateHolder {
            return TrackContextMenuStateHolder(
                arguments = arguments,
                trackRepository = trackRepositoryDep,
                playlistRepository = playlistRepositoryDep,
                queueRepository = queueRepositoryDep,
                navController = navControllerDep,
                stateHolderScope = this,
            )
        }

        "init sets state" -
            {
                "when track has no artists" {
                    val track = FixtureProvider.track(id = TRACK_ID, artistCount = 0)
                    trackRepositoryDep.tracks.value = listOf(track)

                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackContextMenuState.Loading
                        awaitItem() shouldBe
                            TrackContextMenuState.Data(
                                trackName = track.name,
                                trackId = TRACK_ID,
                                viewArtistsState = ViewArtistRow.NoArtists,
                                viewAlbumState = ViewAlbumRow(albumId = track.albumId),
                                removeFromPlaylistRow = null,
                            )
                    }
                }

                "when track has one artist" {
                    val track = FixtureProvider.track(id = TRACK_ID, artistCount = 1)
                    trackRepositoryDep.tracks.value = listOf(track)

                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackContextMenuState.Loading
                        awaitItem() shouldBe
                            TrackContextMenuState.Data(
                                trackName = track.name,
                                trackId = TRACK_ID,
                                viewArtistsState = ViewArtistRow.SingleArtist(track.artists[0].id),
                                viewAlbumState = ViewAlbumRow(albumId = track.albumId),
                                removeFromPlaylistRow = null,
                            )
                    }
                }

                "when track has multiple artists" {
                    val track = FixtureProvider.track(id = TRACK_ID, artistCount = 2)
                    trackRepositoryDep.tracks.value = listOf(track)

                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackContextMenuState.Loading
                        awaitItem() shouldBe
                            TrackContextMenuState.Data(
                                trackName = track.name,
                                trackId = TRACK_ID,
                                viewArtistsState = ViewArtistRow.MultipleArtists,
                                viewAlbumState = ViewAlbumRow(albumId = track.albumId),
                                removeFromPlaylistRow = null,
                            )
                    }
                }

                "when trackList is album" {
                    val track = FixtureProvider.track(id = TRACK_ID, albumId = ALBUM_ID)
                    trackRepositoryDep.tracks.value = listOf(track)

                    val subject =
                        getSubject(
                            arguments =
                                DEFAULT_ARGS.copy(trackList = MediaGroup.Album(albumId = ALBUM_ID))
                        )
                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackContextMenuState.Loading
                        awaitItem() shouldBe
                            TrackContextMenuState.Data(
                                trackName = track.name,
                                trackId = TRACK_ID,
                                viewArtistsState = ViewArtistRow.SingleArtist(track.artists[0].id),
                                viewAlbumState = null,
                                removeFromPlaylistRow = null,
                            )
                    }
                }

                "when trackList is playlist" {
                    val track = FixtureProvider.track(id = TRACK_ID, albumId = ALBUM_ID)
                    trackRepositoryDep.tracks.value = listOf(track)

                    val subject =
                        getSubject(
                            arguments =
                                DEFAULT_ARGS.copy(trackList = MediaGroup.Playlist(PLAYLIST_ID))
                        )
                    testStateHolderState(subject) {
                        awaitItem() shouldBe TrackContextMenuState.Loading
                        awaitItem() shouldBe
                            TrackContextMenuState.Data(
                                trackName = track.name,
                                trackId = TRACK_ID,
                                viewArtistsState = ViewArtistRow.SingleArtist(track.artists[0].id),
                                viewAlbumState = ViewAlbumRow(albumId = track.albumId),
                                removeFromPlaylistRow =
                                    RemoveFromPlaylistRow(
                                        playlistId = PLAYLIST_ID,
                                        trackPositionInPlaylist = TRACK_POSITION_IN_LIST.toLong(),
                                    ),
                            )
                    }
                }
            }

        "handle" -
            {
                "AddToQueueClicked adds track to queue and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(arguments = DEFAULT_ARGS, navController = navControllerDep)
                    )
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.AddToQueueClicked)
                    advanceUntilIdle()
                    queueRepositoryDep.queuedTracks.value.last().track.id shouldBe TRACK_ID
                    navControllerDep.backStack.shouldBeEmpty()
                }

                "ViewAlbumClicked opens album and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(arguments = DEFAULT_ARGS, navController = navControllerDep)
                    )
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewAlbumClicked(albumId = ALBUM_ID))
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    TrackListUiComponent(
                                        arguments =
                                            TrackListArguments(
                                                MediaGroup.Album(albumId = ALBUM_ID)
                                            ),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.Screen,
                            )
                        )
                }

                "ViewArtistClicked opens artist and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(arguments = DEFAULT_ARGS, navController = navControllerDep)
                    )
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewArtistClicked(artistId = 0))
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    ArtistUiComponent(
                                        arguments = ArtistArguments(artistId = 0),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.Screen,
                            )
                        )
                }

                "ViewArtistsClicked opens artists menu and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(arguments = DEFAULT_ARGS, navController = navControllerDep)
                    )
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewArtistsClicked)
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    ArtistsMenu(
                                        arguments =
                                            ArtistsMenuArguments(MediaGroup.SingleTrack(TRACK_ID)),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.BottomSheet,
                            )
                        )
                }

                "RemoveFromPlaylistClicked removes track from playlist and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(arguments = DEFAULT_ARGS, navController = navControllerDep)
                    )
                    playlistRepositoryDep.playlistTrackCrossRef.value =
                        listOf(
                            PlaylistTrackCrossRef(
                                playlistId = PLAYLIST_ID,
                                trackId = TRACK_ID,
                                position = TRACK_POSITION_IN_LIST.toLong(),
                            )
                        )
                    val subject = getSubject()
                    subject.handle(
                        TrackContextMenuUserAction.RemoveFromPlaylistClicked(
                            playlistId = PLAYLIST_ID,
                            trackPositionInPlaylist = TRACK_POSITION_IN_LIST.toLong(),
                        )
                    )
                    advanceUntilIdle()
                    playlistRepositoryDep.playlistTrackCrossRef.value.shouldBeEmpty()
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 0L
        private const val ALBUM_ID = 0L
        private const val PLAYLIST_ID = 0L
        private const val TRACK_POSITION_IN_LIST = 0
        private val DEFAULT_ARGS =
            TrackContextMenuArguments(
                trackId = TRACK_ID,
                trackPositionInList = TRACK_POSITION_IN_LIST,
                trackList = MediaGroup.AllTracks,
            )
    }
}
