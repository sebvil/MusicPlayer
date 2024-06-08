package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.repository.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.util.FixtureProvider
import com.sebastianvm.musicplayer.util.advanceUntilIdle
import com.sebastianvm.musicplayer.util.awaitItemAs
import com.sebastianvm.musicplayer.util.testStateHolderState
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class GenreContextMenuStateHolderTest :
    FreeSpec({
        lateinit var genreRepositoryDep: FakeGenreRepository
        lateinit var playbackManagerDep: FakePlaybackManager

        beforeTest {
            genreRepositoryDep = FakeGenreRepository()
            playbackManagerDep = FakePlaybackManager()
        }

        fun TestScope.getSubject(genreId: Long): GenreContextMenuStateHolder {
            return GenreContextMenuStateHolder(
                arguments = GenreContextMenuArguments(genreId = genreId),
                genreRepository = genreRepositoryDep,
                playbackManager = playbackManagerDep,
                stateHolderScope = this,
            )
        }

        "init sets state" {
            genreRepositoryDep.genres.value = FixtureProvider.genreFixtures()
            val genre = FixtureProvider.genreFixtures().first()
            val subject = getSubject(genre.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe GenreContextMenuState.Loading
                awaitItemAs<GenreContextMenuState.Data>() shouldBe
                    GenreContextMenuState.Data(genreName = genre.genreName, genreId = genre.id)
            }
        }

        "handle" -
            {
                "PlayGenreClicked plays genre" {
                    val genre = FixtureProvider.genreFixtures().first()
                    val subject = getSubject(genre.id)
                    subject.handle(GenreContextMenuUserAction.PlayGenreClicked)
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Genre(genreId = genre.id),
                                initialTrackIndex = 0,
                            )
                        )
                }
            }
    })