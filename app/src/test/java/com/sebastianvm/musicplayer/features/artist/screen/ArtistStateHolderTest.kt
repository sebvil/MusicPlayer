package com.sebastianvm.musicplayer.features.artist.screen

import app.cash.molecule.RecompositionMode
import com.sebastianvm.musicplayer.designsystem.components.AlbumRow
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.resources.RString
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ArtistStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): ArtistStateHolder {
            return ArtistStateHolder(
                stateHolderScope = this,
                arguments = ArtistArguments(ARTIST_ID),
                artistRepository = artistRepositoryDep,
                navController = navControllerDep,
                recompositionMode = RecompositionMode.Immediate,
            )
        }

        "init sets state" -
            {
                "when artist has no albums" {
                    val artist =
                        FixtureProvider.artist(id = ARTIST_ID, albumCount = 0, appearsOnCount = 0)
                    artistRepositoryDep.artists.value = listOf(artist)
                    val subject = getSubject()
                    testStateHolderState(subject) {
                        awaitItem() shouldBe ArtistState.Loading
                        awaitItem() shouldBe
                            ArtistState.Data(
                                artistName = artist.name,
                                artistAlbumsSection = null,
                                artistAppearsOnSection = null,
                            )
                    }
                }

                "when artist has albums" {
                    val artist =
                        FixtureProvider.artist(id = ARTIST_ID, albumCount = 10, appearsOnCount = 10)
                    artistRepositoryDep.artists.value = listOf(artist)
                    val subject = getSubject()

                    testStateHolderState(subject) {
                        awaitItem() shouldBe ArtistState.Loading
                        awaitItem() shouldBe
                            ArtistState.Data(
                                artistName = artist.name,
                                artistAlbumsSection =
                                    ArtistScreenSection(
                                        title = RString.albums,
                                        albums = artist.albums.map { AlbumRow.State.fromAlbum(it) },
                                    ),
                                artistAppearsOnSection =
                                    ArtistScreenSection(
                                        title = RString.appears_on,
                                        albums =
                                            artist.appearsOn.map { AlbumRow.State.fromAlbum(it) },
                                    ),
                            )
                    }
                }
            }

        "handle" -
            {
                "AlbumMoreIconClicked navigates to AlbumContextMenu" {
                    val subject = getSubject()
                    subject.handle(ArtistUserAction.AlbumMoreIconClicked(ALBUM_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                AlbumContextMenu(
                                    arguments = AlbumContextMenuArguments(ALBUM_ID),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.BottomSheet,
                        )
                }

                "AlbumClicked navigates to TrackList" {
                    val subject = getSubject()
                    subject.handle(ArtistUserAction.AlbumClicked(ALBUM_ID))

                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            uiComponent =
                                TrackListUiComponent(
                                    arguments = TrackListArguments(MediaGroup.Album(ALBUM_ID)),
                                    navController = navControllerDep,
                                ),
                            presentationMode = NavOptions.PresentationMode.Screen,
                        )
                }

                "BackClicked pops backstack" {
                    navControllerDep.push(
                        ArtistUiComponent(ArtistArguments(ARTIST_ID), navControllerDep)
                    )
                    val subject = getSubject()
                    subject.handle(ArtistUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val ALBUM_ID = 1L
        private const val ARTIST_ID = 0L
    }
}
