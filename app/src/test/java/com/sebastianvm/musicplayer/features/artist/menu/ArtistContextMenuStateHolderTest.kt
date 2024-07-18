package com.sebastianvm.musicplayer.features.artist.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class ArtistContextMenuStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep:
            com.sebastianvm.musicplayer.core.datatest.artist.FakeArtistRepository
        lateinit var playbackManagerDep: FakePlaybackManager

        beforeTest {
            artistRepositoryDep =
                com.sebastianvm.musicplayer.core.datatest.artist.FakeArtistRepository()
            playbackManagerDep = FakePlaybackManager()
        }

        fun TestScope.getSubject(artistId: Long): ArtistContextMenuStateHolder {
            return ArtistContextMenuStateHolder(
                arguments = ArtistContextMenuArguments(artistId = artistId),
                artistRepository = artistRepositoryDep,
                playbackManager = playbackManagerDep,
                stateHolderScope = this,
            )
        }

        "init sets state" {
            val artists = FixtureProvider.artists()
            artistRepositoryDep.artists.value = artists
            val artist = artists.first()
            val subject = getSubject(artist.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe ArtistContextMenuState.Loading
                awaitItemAs<ArtistContextMenuState.Data>() shouldBe
                    ArtistContextMenuState.Data(artistName = artist.name, artistId = artist.id)
            }
        }

        "handle PlayArtistClicked plays artist" {
            val artist = FixtureProvider.artists().first()
            val subject = getSubject(artist.id)
            subject.handle(ArtistContextMenuUserAction.PlayArtistClicked)
            advanceUntilIdle()
            playbackManagerDep.playMediaInvocations shouldBe
                listOf(
                    FakePlaybackManager.PlayMediaArguments(
                        mediaGroup = MediaGroup.Artist(artistId = artist.id),
                        initialTrackIndex = 0,
                    )
                )
        }
    })
