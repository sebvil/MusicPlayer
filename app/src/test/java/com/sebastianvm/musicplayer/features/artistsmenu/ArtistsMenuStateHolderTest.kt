package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.database.entities.AlbumsForArtist
import com.sebastianvm.musicplayer.database.entities.ArtistTrackCrossRef
import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.player.HasArtists
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
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
            return ArtistsMenuStateHolder(
                arguments = ArtistsMenuArguments(media = media),
                artistRepository = artistRepositoryDep,
                navController = navControllerDep,
                stateHolderScope = this,
            )
        }

        "init sets state" -
            {
                "for Album" {
                    val artists = FixtureProvider.artistFixtures()
                    artistRepositoryDep.albumsForArtists.value =
                        artists.map {
                            AlbumsForArtist(
                                albumId = ALBUM_ID,
                                artistId = it.id,
                                artistName = it.artistName,
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
                    val artists = FixtureProvider.artistFixtures()
                    artistRepositoryDep.artistTrackCrossRefs.value =
                        artists.map {
                            ArtistTrackCrossRef(
                                trackId = TRACK_ID,
                                artistId = it.id,
                                artistName = it.artistName,
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
                    navControllerDep.push(
                        ArtistsMenu(
                            arguments = ArtistsMenuArguments(media = MediaGroup.Album(ALBUM_ID)),
                            navController = navControllerDep,
                        ),
                        navOptions =
                            NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                    )
                    val subject = getSubject()
                    subject.handle(ArtistsMenuUserAction.ArtistClicked(ARTIST_ID))
                    navControllerDep.backStack.last() shouldBe
                        BackStackEntry(
                            ArtistUiComponent(
                                arguments = ArtistArguments(ARTIST_ID),
                                navController = navControllerDep,
                            ),
                            presentationMode = NavOptions.PresentationMode.Screen,
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
