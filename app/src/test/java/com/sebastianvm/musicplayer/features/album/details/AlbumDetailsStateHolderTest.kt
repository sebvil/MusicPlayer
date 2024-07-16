package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class AlbumDetailsStateHolderTest :
    FreeSpec({
        lateinit var albumRepositoryDep:
            com.sebastianvm.musicplayer.core.datatest.album.FakeAlbumRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            albumRepositoryDep =
                com.sebastianvm.musicplayer.core.datatest.album.FakeAlbumRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            album: Album = FixtureProvider.album(id = ALBUM_ID)
        ): AlbumDetailsStateHolder {
            albumRepositoryDep.albums.value = listOf(album)
            val arguments =
                AlbumDetailsArguments(
                    albumId = album.id,
                    albumName = album.title,
                    imageUri = album.imageUri,
                    artists = album.artists.joinToString { it.name },
                )
            navControllerDep.push(
                uiComponent =
                    AlbumDetailsUiComponent(
                        arguments = arguments, navController = navControllerDep))
            return AlbumDetailsStateHolder(
                stateHolderScope = this,
                args = arguments,
                navController = navControllerDep,
                playbackManager = playbackManagerDep,
                albumRepository = albumRepositoryDep,
            )
        }

        "init subscribes to changes in album" {
            val album = FixtureProvider.album()
            val subject = getSubject(album = album)

            testStateHolderState(subject) {
                awaitItem() shouldBe
                    AlbumDetailsState.Loading(
                        albumName = album.title,
                        imageUri = album.imageUri,
                        artists = album.artists.joinToString { it.name },
                    )

                awaitItem() shouldBe
                    AlbumDetailsState.Data(
                        albumName = album.title,
                        imageUri = album.imageUri,
                        tracks = album.tracks.map { TrackRow.State.fromTrack(it) },
                        artists = album.artists.joinToString { it.name },
                    )

                val updatedAlbum = FixtureProvider.album(id = album.id)
                albumRepositoryDep.albums.value = listOf(updatedAlbum)

                awaitItem() shouldBe
                    AlbumDetailsState.Data(
                        albumName = updatedAlbum.title,
                        imageUri = updatedAlbum.imageUri,
                        tracks = updatedAlbum.tracks.map { TrackRow.State.fromTrack(it) },
                        artists = updatedAlbum.artists.joinToString { it.name },
                    )
            }
        }

        "handle" -
            {
                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(AlbumDetailsUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Album(albumId = ALBUM_ID),
                                initialTrackIndex = TRACK_INDEX,
                            ))
                }

                "TrackMoreIconClicked navigates to TrackContextMenu" {
                    val subject = getSubject()
                    subject.handle(
                        AlbumDetailsUserAction.TrackMoreIconClicked(TRACK_ID, TRACK_INDEX))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackContextMenu(
                                    arguments =
                                        TrackContextMenuArguments(
                                            trackId = TRACK_ID,
                                            trackPositionInList = TRACK_INDEX,
                                            trackList = MediaGroup.Album(albumId = ALBUM_ID),
                                        ),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }

                "BackClicked navigates back" {
                    val subject = getSubject()
                    subject.handle(AlbumDetailsUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val TRACK_ID = 1L
        private const val TRACK_INDEX = 0
        private const val ALBUM_ID = 1L
    }
}
