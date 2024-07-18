package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.awaitItemAs
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.genre.FakeGenreRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
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
            val genres = FixtureProvider.genres()
            genreRepositoryDep.genres.value = genres
            val genre = genres.first()
            val subject = getSubject(genre.id)
            testStateHolderState(subject) {
                awaitItem() shouldBe GenreContextMenuState.Loading
                awaitItemAs<GenreContextMenuState.Data>() shouldBe
                    GenreContextMenuState.Data(genreName = genre.name, genreId = genre.id)
            }
        }

        "handle" -
            {
                "PlayGenreClicked plays genre" {
                    val subject = getSubject(GENRE_ID)
                    subject.handle(GenreContextMenuUserAction.PlayGenreClicked)
                    advanceUntilIdle()
                    playbackManagerDep.playMediaInvocations shouldBe
                        listOf(
                            FakePlaybackManager.PlayMediaArguments(
                                mediaGroup = MediaGroup.Genre(genreId = GENRE_ID),
                                initialTrackIndex = 0,
                            ))
                }
            }
    }) {
    companion object {
        private const val GENRE_ID = 0L
    }
}
