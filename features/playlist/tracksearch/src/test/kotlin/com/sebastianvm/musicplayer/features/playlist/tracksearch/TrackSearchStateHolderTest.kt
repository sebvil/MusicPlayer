package com.sebastianvm.musicplayer.features.playlist.tracksearch

import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.fts.FakeFullTextSearchRepository
import com.sebastianvm.musicplayer.core.datatest.playlist.FakePlaylistRepository
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.Playlist
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TrackSearchStateHolderTest :
    FreeSpec({
        lateinit var playlistRepositoryDep: FakePlaylistRepository
        lateinit var ftsRepositoryDep: FakeFullTextSearchRepository
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            playlistRepositoryDep = FakePlaylistRepository()
            ftsRepositoryDep = FakeFullTextSearchRepository()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            playlist: Playlist = FixtureProvider.playlist(),
            resultsMap: Map<String, List<Track>> = emptyMap(),
        ): TrackSearchStateHolder {
            ftsRepositoryDep.trackQueryToResultsMap.value = resultsMap
            playlistRepositoryDep.playlists.value = listOf(playlist)
            navControllerDep.push(
                TrackSearchMvvmComponent(
                    arguments = TrackSearchArguments(playlistId = playlist.id),
                    navController = navControllerDep,
                )
            )
            return TrackSearchStateHolder(
                arguments = TrackSearchArguments(playlistId = playlist.id),
                playlistRepository = playlistRepositoryDep,
                ftsRepository = ftsRepositoryDep,
                navController = navControllerDep,
                viewModelScope = this,
            )
        }

        "init subscribes to changes in playlistracks" {
            val queryResults = FixtureProvider.tracks(size = 10)
            val results = mapOf("" to queryResults)
            val playlist = FixtureProvider.playlist().copy(tracks = queryResults.subList(0, 5))
            val subject = getSubject(playlist, results)
            println(queryResults)
            testStateHolderState(subject) {
                awaitItem().trackSearchResults.shouldBeEmpty()

                awaitItem() shouldBe
                        TrackSearchState(
                            trackSearchResults =
                            (queryResults.subList(5, queryResults.size) +
                                    queryResults.subList(0, 5))
                                .map { track ->
                                    TrackSearchResult(
                                        state = TrackRow.State.fromTrack(track),
                                        inPlaylist = track.id in playlist.tracks.map { it.id },
                                    )
                                },
                            trackAddedToPlaylist = null,
                        )
            }
        }

        "handle" -
                {
                    "TextChanged updates search results" {
                        val queryResults = FixtureProvider.tracks()
                        val results = mapOf("" to emptyList(), QUERY to queryResults)
                        val playlist = FixtureProvider.playlist()
                        val subject = getSubject(playlist = playlist, resultsMap = results)

                        testStateHolderState(subject) {
                            awaitItem() shouldBe TrackSearchState(trackSearchResults = emptyList())
                            subject.handle(TrackSearchUserAction.TextChanged(QUERY))

                            awaitItem() shouldBe
                                    TrackSearchState(
                                        trackSearchResults =
                                        queryResults.map {
                                            TrackSearchResult(
                                                state = TrackRow.State.fromTrack(it),
                                                inPlaylist = false,
                                            )
                                        }
                                    )
                        }
                    }

                    "TrackClicked adds track to playlist and shows toast, and ToastShown dismisses toast" {
                        val queryResults = FixtureProvider.tracks()
                        val results = mapOf("" to queryResults)
                        val track = queryResults.first()
                        val subject =
                            getSubject(
                                playlist = FixtureProvider.playlist(trackCount = 0),
                                resultsMap = results,
                            )

                        testStateHolderState(subject) {
                            skipItems(2)
                            subject.handle(TrackSearchUserAction.TrackClicked(track.id, track.name))
                            awaitItem() shouldBe
                                    TrackSearchState(
                                        trackSearchResults =
                                        queryResults
                                            .map {
                                                TrackSearchResult(
                                                    state = TrackRow.State.fromTrack(it),
                                                    inPlaylist = it.id == track.id,
                                                )
                                            }
                                            .toMutableList()
                                            .apply { add(removeAt(0)) },
                                        trackAddedToPlaylist = track.name,
                                    )

                            subject.handle(TrackSearchUserAction.ToastShown)
                            awaitItem().trackAddedToPlaylist shouldBe null
                        }
                    }

                    "BackClicked pops back stack" {
                        val subject = getSubject()

                        subject.handle(TrackSearchUserAction.BackClicked)

                        navControllerDep.backStack.isEmpty() shouldBe true
                    }
                }
    }) {
    companion object {
        private const val QUERY = "a"
    }
}
