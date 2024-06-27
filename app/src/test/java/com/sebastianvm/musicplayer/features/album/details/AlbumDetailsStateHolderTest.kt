package com.sebastianvm.musicplayer.features.album.details

import app.cash.molecule.RecompositionMode
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.assertThat
import com.sebastianvm.musicplayer.util.shouldBe
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty

class AlbumDetailsStateHolderTest :
    FreeSpec({
        lateinit var albumRepositoryDep: FakeAlbumRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController
        lateinit var album: Album

        beforeTest {
            albumRepositoryDep = FakeAlbumRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
            album = FixtureProvider.album(id = ALBUM_ID)
        }

        fun TestScope.getSubject(): AlbumDetailsStateHolder {
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
                recompositionMode = RecompositionMode.Immediate)
        }

        "init subscribes to changes in album" {
            val subject = getSubject()

            testStateHolderState(subject) {
                assertThat(awaitItem()) shouldBe
                    AlbumDetailsState.Loading(
                        albumName = album.title,
                        imageUri = album.imageUri,
                        artists = album.artists.joinToString { it.name },
                    )

                assertThat(awaitItem()) shouldBe
                    AlbumDetailsState.Data(
                        albumName = album.title,
                        imageUri = album.imageUri,
                        tracks = album.tracks.map { TrackRow.State.fromTrack(it) },
                        artists = album.artists.joinToString { it.name },
                    )

                val updatedAlbum = FixtureProvider.album(id = album.id, artistCount = 0)
                albumRepositoryDep.albums.value = listOf(updatedAlbum)

                assertThat(awaitItem()) shouldBe
                    AlbumDetailsState.Data(
                        albumName = updatedAlbum.title,
                        imageUri = updatedAlbum.imageUri,
                        tracks = updatedAlbum.tracks.map { TrackRow.State.fromTrack(it) },
                        artists = null,
                    )
            }
        }

        "handle" -
            {
                "TrackClicked plays media" {
                    val subject = getSubject()
                    subject.handle(AlbumDetailsUserAction.TrackClicked(TRACK_INDEX))
                    advanceUntilIdle()
                    assertThat(playbackManagerDep.playMediaInvocations) shouldBe
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
                    assertThat(navControllerDep.backStack.last()) shouldBe
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
    })

private const val TRACK_ID = 1L
private const val TRACK_INDEX = 0
private const val ALBUM_ID = 1L
