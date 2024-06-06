package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.repository.queue.FakeQueueRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class AlbumContextMenuStateHolderTest : FreeSpec({
    lateinit var albumRepositoryDep: FakeAlbumRepository
    lateinit var playbackManagerDep: FakePlaybackManager
    lateinit var queueRepositoryDep: FakeQueueRepository
    lateinit var navControllerDep: FakeNavController

    beforeTest {
        albumRepositoryDep = FakeAlbumRepository()
        playbackManagerDep = FakePlaybackManager()
        queueRepositoryDep = FakeQueueRepository()
        navControllerDep = FakeNavController()
    }

    fun TestScope.getSubject(albumId: Long): AlbumContextMenuStateHolder {
        return AlbumContextMenuStateHolder(
            arguments = AlbumContextMenuArguments(albumId = albumId),
            stateHolderScope = this,
            albumRepository = albumRepositoryDep,
            queueRepository = queueRepositoryDep,
            playbackManager = playbackManagerDep,
            navController = navControllerDep,
        )
    }

    "init sets state" - {
        "when album has no artists" {
            albumRepositoryDep.albums.value = FixtureProvider.albumFixtures()
            val album = FixtureProvider.albumFixtures().first()
            val subject = getSubject(album.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe AlbumContextMenuState.Loading
                awaitItemAs<AlbumContextMenuState.Data>() shouldBe AlbumContextMenuState.Data(
                    albumName = album.albumName,
                    albumId = album.id,
                    viewArtistsState = ViewArtistRow.NoArtists
                )
            }
        }

        "when album has one artist" {
            albumRepositoryDep.albums.value = FixtureProvider.albumFixtures()
            val album = FixtureProvider.albumFixtures().first()
            val artist = FixtureProvider.artistFixtures().first()
            albumRepositoryDep.albumsForArtist.value = listOf(
                AlbumsForArtist(
                    albumId = album.id,
                    artistId = artist.id,
                    artistName = artist.artistName,
                    albumName = album.albumName,
                    year = album.year
                )
            )
            val subject = getSubject(album.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe AlbumContextMenuState.Loading
                awaitItemAs<AlbumContextMenuState.Data>() shouldBe AlbumContextMenuState.Data(
                    albumName = album.albumName,
                    albumId = album.id,
                    viewArtistsState = ViewArtistRow.SingleArtist(artist.id)
                )
            }
        }

        "when album has multiple artists" {
            albumRepositoryDep.albums.value = FixtureProvider.albumFixtures()
            val album = FixtureProvider.albumFixtures().first()
            albumRepositoryDep.albumsForArtist.value =
                FixtureProvider.artistFixtures().map { artist ->
                    AlbumsForArtist(
                        albumId = album.id,
                        artistId = artist.id,
                        artistName = artist.artistName,
                        albumName = album.albumName,
                        year = album.year
                    )
                }
            val subject = getSubject(album.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe AlbumContextMenuState.Loading
                awaitItemAs<AlbumContextMenuState.Data>() shouldBe AlbumContextMenuState.Data(
                    albumName = album.albumName,
                    albumId = album.id,
                    viewArtistsState = ViewArtistRow.MultipleArtists
                )
            }
        }
    }

    "handle" - {
        "AddToQueueClicked adds tracks to queue" {
            val album = FixtureProvider.albumFixtures().first()
            val subject = getSubject(album.id)
            subject.handle(AlbumContextMenuUserAction.AddToQueueClicked)
            advanceUntilIdle()
            queueRepositoryDep.addToQueueInvocations shouldBe listOf(
                FakeQueueRepository.AddToQueueArguments(mediaGroup = MediaGroup.Album(albumId = album.id))
            )
        }

        "PlayAlbumClicked plays album" {
            val album = FixtureProvider.albumFixtures().first()
            val subject = getSubject(album.id)
            subject.handle(AlbumContextMenuUserAction.PlayAlbumClicked)
            advanceUntilIdle()
            playbackManagerDep.playMediaInvocations shouldBe listOf(
                FakePlaybackManager.PlayMediaArguments(
                    mediaGroup = MediaGroup.Album(albumId = album.id),
                    initialTrackIndex = 0
                )
            )
        }

        "ViewArtistClicked shows artist screen" {
            navControllerDep.push(
                AlbumContextMenu(
                    arguments = AlbumContextMenuArguments(
                        albumId = 1
                    ),
                    navController = navControllerDep
                ),
                navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
            )
            val album = FixtureProvider.albumFixtures().first()
            val subject = getSubject(album.id)
            subject.handle(AlbumContextMenuUserAction.ViewArtistClicked(ARTIST_ID))
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                ArtistUiComponent(
                    arguments = ArtistArguments(ARTIST_ID),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.Screen
            )
        }

        "ViewArtistsClicked shows artists menu" {
            navControllerDep.push(
                AlbumContextMenu(
                    arguments = AlbumContextMenuArguments(
                        albumId = 1
                    ),
                    navController = navControllerDep
                ),
                navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
            )
            val album = FixtureProvider.albumFixtures().first()
            val subject = getSubject(album.id)
            subject.handle(AlbumContextMenuUserAction.ViewArtistsClicked)
            navControllerDep.backStack.last() shouldBe BackStackEntry(
                ArtistsMenu(
                    arguments = ArtistsMenuArguments(MediaGroup.Album(albumId = album.id)),
                    navController = navControllerDep
                ),
                presentationMode = NavOptions.PresentationMode.BottomSheet
            )
        }
    }
}) {
    companion object {
        private const val ARTIST_ID = 1L
    }
}
