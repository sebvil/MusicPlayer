package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.datatest.album.FakeAlbumRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuProps
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class AlbumContextMenuViewModelTest :
    FreeSpec({
        lateinit var albumRepositoryDep: FakeAlbumRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            albumRepositoryDep = FakeAlbumRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(albumId: Long): AlbumContextMenuViewModel {
            val arguments = AlbumContextMenuArguments(albumId = albumId)
            navControllerDep.push(FakeMvvmComponent())
            return AlbumContextMenuViewModel(
                arguments = arguments,
                viewModelScope = this,
                albumRepository = albumRepositoryDep,
                playbackManager = playbackManagerDep,
                props = MutableStateFlow(AlbumContextMenuProps(navControllerDep)),
                features = FakeFeatures(),
            )
        }

        "init sets state" -
            {
                "when album has no artists" {
                    val album = FixtureProvider.album(artistCount = 0)
                    albumRepositoryDep.albums.value = listOf(album)
                    val subject = getSubject(album.id)
                    testViewModelState(subject) {
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
                    testViewModelState(subject) {
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
                    testViewModelState(subject) {
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
                            )
                        )
                }

                "ViewArtistClicked shows artist screen" {
                    val album = FixtureProvider.albums().first()
                    val subject = getSubject(album.id)
                    subject.handle(AlbumContextMenuUserAction.ViewArtistClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            mvvmComponent =
                                FakeMvvmComponent(arguments = ArtistDetailsArguments(ARTIST_ID)),
                            navOptions =
                                NavOptions(
                                    popCurrent = true,
                                    presentationMode = NavOptions.PresentationMode.Screen,
                                ),
                        )
                }

                "ViewArtistsClicked shows artists menu" {
                    val album = FixtureProvider.albums().first()
                    val subject = getSubject(album.id)
                    subject.handle(AlbumContextMenuUserAction.ViewArtistsClicked)
                    navControllerDep.backStack.last() shouldBe
                        FakeBackstackEntry(
                            FakeMvvmComponent(
                                arguments =
                                    ArtistsMenuArguments(MediaGroup.Album(albumId = album.id))
                            ),
                            navOptions =
                                NavOptions(
                                    popCurrent = true,
                                    presentationMode = NavOptions.PresentationMode.BottomSheet,
                                ),
                        )
                }
            }
    }) {
    companion object {
        private const val ARTIST_ID = 1L
    }
}
