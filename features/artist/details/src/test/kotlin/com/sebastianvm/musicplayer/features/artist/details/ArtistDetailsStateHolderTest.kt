package com.sebastianvm.musicplayer.features.artist.details

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ArtistDetailsStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(): ArtistDetailsStateHolder {
            return ArtistDetailsStateHolder(
                stateHolderScope = this,
                arguments = ArtistDetailsArguments(ARTIST_ID),
                artistRepository = artistRepositoryDep,
                navController = navControllerDep,
                features = initializeFakeFeatures(),
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
                        awaitItem() shouldBe ArtistDetailsState.Loading
                        awaitItem() shouldBe
                            ArtistDetailsState.Data(
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
                        awaitItem() shouldBe ArtistDetailsState.Loading
                        awaitItem() shouldBe
                            ArtistDetailsState.Data(
                                artistName = artist.name,
                                artistAlbumsSection =
                                    ArtistDetailsSection(
                                        title = RString.albums,
                                        albums = artist.albums.map { AlbumRow.State.fromAlbum(it) },
                                    ),
                                artistAppearsOnSection =
                                    ArtistDetailsSection(
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
                    subject.handle(ArtistDetailsUserAction.AlbumMoreIconClicked(ALBUM_ID))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            uiComponent =
                                FakeUiComponent(
                                    name = "AlbumContextMenu",
                                    arguments = AlbumContextMenuArguments(ALBUM_ID),
                                ),
                            navOptions =
                                NavOptions(
                                    presentationMode = NavOptions.PresentationMode.BottomSheet
                                ),
                        )
                }

                "AlbumClicked navigates to TrackList" {
                    val subject = getSubject()
                    subject.handle(
                        ArtistDetailsUserAction.AlbumClicked(
                            albumItem =
                                AlbumRow.State(
                                    id = ALBUM_ID,
                                    albumName = ALBUM_NAME,
                                    artworkUri = IMAGE_URI,
                                    artists = ARTIST_NAME,
                                )
                        )
                    )

                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            uiComponent =
                                FakeUiComponent(
                                    name = "AlbumDetails",
                                    arguments =
                                        AlbumDetailsArguments(
                                            albumId = ALBUM_ID,
                                            albumName = ALBUM_NAME,
                                            imageUri = IMAGE_URI,
                                            artists = ARTIST_NAME,
                                        ),
                                ),
                            navOptions =
                                NavOptions(presentationMode = NavOptions.PresentationMode.Screen),
                        )
                }

                "BackClicked pops backstack" {
                    navControllerDep.push(
                        ArtistDetailsUiComponent(
                            ArtistDetailsArguments(ARTIST_ID),
                            navControllerDep,
                        )
                    )
                    val subject = getSubject()
                    subject.handle(ArtistDetailsUserAction.BackClicked)
                    navControllerDep.backStack.shouldBeEmpty()
                }
            }
    }) {
    companion object {
        private const val ALBUM_ID = 1L
        private const val ARTIST_ID = 0L
        private const val ALBUM_NAME = "Album 1"
        private const val IMAGE_URI = "imageUri"
        private const val ARTIST_NAME = "Artist 1"
    }
}
