package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.artist.details.ArtistDetailsUiComponent
import com.sebastianvm.musicplayer.features.artistsmenu.ArtistsMenu
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.services.featureRegistry.album.menu.AlbumContextMenuArguments
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class AlbumContextMenuStateHolderTest :
    FreeSpec({
        lateinit var albumRepositoryDep: FakeAlbumRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            albumRepositoryDep = FakeAlbumRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(albumId: Long): AlbumContextMenuStateHolder {
            return AlbumContextMenuStateHolder(
                arguments = AlbumContextMenuArguments(albumId = albumId),
                stateHolderScope = this,
                albumRepository = albumRepositoryDep,
                playbackManager = playbackManagerDep,
                navController = navControllerDep,
            )
        }

        "init sets state" -
            {
                "when album has no artists" {
                    val album = FixtureProvider.album(artistCount = 0)
                    albumRepositoryDep.albums.value = listOf(album)
                    val subject = getSubject(album.id)
                    testStateHolderState(subject) {
                        awaitItem() shouldBe AlbumContextMenuState.Loading
                        awaitItemAs<AlbumContextMenuState.Data>() shouldBe
                            AlbumContextMenuState.Data(
                                albumName = album.title,
                                albumId = album.id,
                                viewArtistsState = ViewArtistRow.NoArtists,
                            )
                    }
                }

                "when album has one artist" {
                    val album = FixtureProvider.album(artistCount = 1)
                    albumRepositoryDep.albums.value = listOf(album)
                    val artist = album.artists.first()

                    val subject = getSubject(album.id)
                    testStateHolderState(subject) {
                        awaitItem() shouldBe AlbumContextMenuState.Loading
                        awaitItemAs<AlbumContextMenuState.Data>() shouldBe
                            AlbumContextMenuState.Data(
                                albumName = album.title,
                                albumId = album.id,
                                viewArtistsState = ViewArtistRow.SingleArtist(artist.id),
                            )
                    }
                }

                "when album has multiple artists" {
                    val album = FixtureProvider.album(artistCount = 2)
                    albumRepositoryDep.albums.value = listOf(album)
                    val subject = getSubject(album.id)
                    testStateHolderState(subject) {
                        awaitItem() shouldBe AlbumContextMenuState.Loading
                        awaitItemAs<AlbumContextMenuState.Data>() shouldBe
                            AlbumContextMenuState.Data(
                                albumName = album.title,
                                albumId = album.id,
                                viewArtistsState = ViewArtistRow.MultipleArtists,
                            )
                    }
                }
            }

        "handle" -
            {
                "PlayAlbumClicked plays album" {
                    val album = FixtureProvider.albums().first()
                    val subject = getSubject(album.id)
                    subject.handle(AlbumContextMenuUserAction.PlayAlbumClicked)
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Album(albumId = album.id),
                                initialTrackIndex = 0,
                            ))
                }

                "ViewArtistClicked shows artist screen" {
                    navControllerDep.push(
                        AlbumContextMenu(
                            arguments = AlbumContextMenuArguments(albumId = 1),
                            navController = navControllerDep,
                        ),
                        navOptions =
                            NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                    )
                    val album = FixtureProvider.albums().first()
                    val subject = getSubject(album.id)
                    subject.handle(AlbumContextMenuUserAction.ViewArtistClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            ArtistDetailsUiComponent(
                                arguments = ArtistDetailsArguments(ARTIST_ID),
                                navController = navControllerDep,
                            ),
                            presentationMode = NavOptions.PresentationMode.Screen,
                        )
                }

                "ViewArtistsClicked shows artists menu" {
                    navControllerDep.push(
                        AlbumContextMenu(
                            arguments = AlbumContextMenuArguments(albumId = 1),
                            navController = navControllerDep,
                        ),
                        navOptions =
                            NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                    )
                    val album = FixtureProvider.albums().first()
                    val subject = getSubject(album.id)
                    subject.handle(AlbumContextMenuUserAction.ViewArtistsClicked)
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            ArtistsMenu(
                                arguments =
                                    ArtistsMenuArguments(MediaGroup.Album(albumId = album.id)),
                                navController = navControllerDep,
                            ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }
            }
    }) {
    companion object {
        private const val ARTIST_ID = 1L
    }
}
