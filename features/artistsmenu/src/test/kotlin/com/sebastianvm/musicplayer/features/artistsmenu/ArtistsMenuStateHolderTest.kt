package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.model.HasArtists
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.test.initializeFakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class ArtistsMenuStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            media: HasArtists = MediaGroup.Album(ALBUM_ID)
        ): ArtistsMenuStateHolder {
            navControllerDep.push(
                ArtistsMenuMvvmComponent(
                    arguments = ArtistsMenuArguments(media = MediaGroup.Album(ALBUM_ID)),
                    navController = navControllerDep,
                ),
                navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
            )
            return ArtistsMenuStateHolder(
                viewModelScope = this,
                arguments = ArtistsMenuArguments(media = media),
                artistRepository = artistRepositoryDep,
                navController = navControllerDep,
                features = initializeFakeFeatures(),
            )
        }

        "init sets state" -
                {
                    "for Album" {
                        val artists = FixtureProvider.artists()
                        artistRepositoryDep.albumsForArtists.value =
                            artists.map {
                                com.sebastianvm.musicplayer.core.database.entities.AlbumsForArtist(
                                    albumId = ALBUM_ID,
                                    artistId = it.id,
                                    artistName = it.name,
                                    albumName = "",
                                    year = 0,
                                )
                            }
                        artistRepositoryDep.artists.value = artists
                        val subject = getSubject(media = MediaGroup.Album(ALBUM_ID))
                        testStateHolderState(subject) {
                            awaitItem() shouldBe Loading
                            awaitItemAs<Data<ArtistsMenuState>>().state shouldBe
                                    ArtistsMenuState(
                                        artists =
                                        artists.map { artist -> ArtistRow.State.fromArtist(artist) }
                                    )
                        }
                    }

                    "for Track" {
                        val artists = FixtureProvider.artists()
                        artistRepositoryDep.artistTrackCrossRefs.value =
                            artists.map {
                                com.sebastianvm.musicplayer.core.database.entities.ArtistTrackCrossRef(
                                    trackId = TRACK_ID,
                                    artistId = it.id,
                                    artistName = it.name,
                                    trackName = "",
                                )
                            }
                        artistRepositoryDep.artists.value = artists
                        val subject = getSubject(media = MediaGroup.SingleTrack(TRACK_ID))
                        testStateHolderState(subject) {
                            awaitItem() shouldBe Loading
                            awaitItemAs<Data<ArtistsMenuState>>().state shouldBe
                                    ArtistsMenuState(
                                        artists =
                                        artists.map { artist -> ArtistRow.State.fromArtist(artist) }
                                    )
                        }
                    }
                }

        "handle" -
                {
                    "ArtistClicked navigates to ArtistScreen" {
                        val subject = getSubject()
                        subject.handle(ArtistsMenuUserAction.ArtistClicked(ARTIST_ID))
                        navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    FakeMvvmComponent(
                                        name = "ArtistDetails",
                                        arguments = ArtistDetailsArguments(ARTIST_ID),
                                    ),
                                    navOptions =
                                    NavOptions(
                                        popCurrent = true,
                                        presentationMode = NavOptions.PresentationMode.Screen,
                                    ),
                                )
                    }
                }
    }) {
    companion object {
        private const val ALBUM_ID = 1L
        private const val TRACK_ID = 1L
        private const val ARTIST_ID = 1L
    }
}
