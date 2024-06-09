package com.sebastianvm.musicplayer.features.artist.menu

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.FakeArtistRepository
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class ArtistContextMenuStateHolderTest :
    FreeSpec({
        lateinit var artistRepositoryDep: FakeArtistRepository
        lateinit var playbackManagerDep: FakePlaybackManager

        beforeTest {
            artistRepositoryDep = FakeArtistRepository()
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
