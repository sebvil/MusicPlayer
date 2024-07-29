package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.core.datatest.track.FakeTrackRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.details.ArtistDetailsUiComponent
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuUiComponent
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.services.featureRegistry.album.details.AlbumDetailsArguments
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class TrackContextMenuStateHolderTest :
    FreeSpec({
        lateinit var trackRepositoryDep: FakeTrackRepository
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var navControllerDep: FakeNavController
        lateinit var playbackManagerDep: FakePlaybackManager

        beforeTest {
            trackRepositoryDep = FakeTrackRepository()
            playlistRepositoryDep = FakePlaylistRepository()
            navControllerDep = FakeNavController()
            playbackManagerDep = FakePlaybackManager()
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
                playbackManager = playbackManagerDep,
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
                                DEFAULT_ARGS.copy(trackList = MediaGroup.Album(albumId = ALBUM_ID)))
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
                                DEFAULT_ARGS.copy(trackList = MediaGroup.Playlist(PLAYLIST_ID)))
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
                        TrackContextMenu(
                            arguments = DEFAULT_ARGS, navController = navControllerDep))
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.AddToQueueClicked)
                    advanceUntilIdle()
                    playbackManagerDep.queuedTracks.value.last().track.id shouldBe TRACK_ID
                    navControllerDep.backStack.shouldBeEmpty()
                }

                "ViewAlbumClicked opens album and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(
                            arguments = DEFAULT_ARGS, navController = navControllerDep))
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewAlbumClicked(albumId = ALBUM_ID))
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    AlbumDetailsUiComponent(
                                        arguments =
                                            AlbumDetailsArguments(
                                                albumId = ALBUM_ID,
                                                albumName = "",
                                                imageUri = "",
                                                artists = "",
                                            ),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.Screen,
                            ))
                }

                "ViewArtistClicked opens artist and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(
                            arguments = DEFAULT_ARGS, navController = navControllerDep))
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewArtistClicked(artistId = 0))
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    ArtistDetailsUiComponent(
                                        arguments = ArtistDetailsArguments(artistId = 0),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.Screen,
                            ))
                }

                "ViewArtistsClicked opens artists menu and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(
                            arguments = DEFAULT_ARGS, navController = navControllerDep))
                    val subject = getSubject()
                    subject.handle(TrackContextMenuUserAction.ViewArtistsClicked)
                    navControllerDep.backStack shouldBe
                        listOf(
                            BackStackEntry(
                                uiComponent =
                                    ArtistsMenuUiComponent(
                                        arguments =
                                            ArtistsMenuArguments(MediaGroup.SingleTrack(TRACK_ID)),
                                        navController = navControllerDep,
                                    ),
                                presentationMode = NavOptions.PresentationMode.BottomSheet,
                            ))
                }

                "RemoveFromPlaylistClicked removes track from playlist and pops screen" {
                    navControllerDep.push(
                        TrackContextMenu(
                            arguments = DEFAULT_ARGS, navController = navControllerDep))
                    playlistRepositoryDep.playlists.value =
                        listOf(FixtureProvider.playlist(id = PLAYLIST_ID, trackCount = TRACK_COUNT))
                    val subject = getSubject()
                    subject.handle(
                        TrackContextMenuUserAction.RemoveFromPlaylistClicked(
                            playlistId = PLAYLIST_ID,
                            trackPositionInPlaylist = TRACK_POSITION_IN_LIST.toLong(),
                        ))
                    advanceUntilIdle()
                    playlistRepositoryDep.playlists.value
                        .first { it.id == PLAYLIST_ID }
                        .tracks shouldHaveSize TRACK_COUNT - 1
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 0L
        private const val ALBUM_ID = 0L
        private const val PLAYLIST_ID = 0L
        private const val TRACK_POSITION_IN_LIST = 0
        private const val TRACK_COUNT = 10
        private val DEFAULT_ARGS =
            TrackContextMenuArguments(
                trackId = TRACK_ID,
                trackPositionInList = TRACK_POSITION_IN_LIST,
                trackList = MediaGroup.AllTracks,
            )
    }
}
