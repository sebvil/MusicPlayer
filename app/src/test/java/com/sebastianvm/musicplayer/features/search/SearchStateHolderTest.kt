package com.sebastianvm.musicplayer.features.search

import android.app.SearchManager.QUERY
import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.testStateHolderState
import com.sebastianvm.musicplayer.core.datatest.extensions.toAlbumWithArtists
import com.sebastianvm.musicplayer.core.datatest.extensions.toBasicArtist
import com.sebastianvm.musicplayer.core.datatest.extensions.toBasicGenre
import com.sebastianvm.musicplayer.core.datatest.extensions.toBasicPlaylist
import com.sebastianvm.musicplayer.core.datatest.fts.FakeFullTextSearchRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.Track
import com.sebastianvm.musicplayer.core.servicestest.features.navigation.FakeNavController
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsUiComponent
import com.sebastianvm.musicplayer.features.navigation.BackStackEntry
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsUiComponent
import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsArguments
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe

class SearchStateHolderTest :
    FreeSpec({
        lateinit var ftsRepositoryDep: FakeFullTextSearchRepository
        lateinit var playbackManagerDep: FakePlaybackManager
        lateinit var navControllerDep: FakeNavController

        beforeTest {
            ftsRepositoryDep = FakeFullTextSearchRepository()
            playbackManagerDep = FakePlaybackManager()
            navControllerDep = FakeNavController()
        }

        fun TestScope.getSubject(
            trackResultsMap: Map<String, List<Track>> = emptyMap(),
            artistResultsMap: Map<String, List<BasicArtist>> = emptyMap(),
            albumResultsMap: Map<String, List<AlbumWithArtists>> = emptyMap(),
            genreResultsMap: Map<String, List<BasicGenre>> = emptyMap(),
            playlistResultsMap: Map<String, List<BasicPlaylist>> = emptyMap(),
        ): SearchStateHolder {
            ftsRepositoryDep.trackQueryToResultsMap.value = trackResultsMap
            ftsRepositoryDep.artistQueryToResultsMap.value = artistResultsMap
            ftsRepositoryDep.albumQueryToResultsMap.value = albumResultsMap
            ftsRepositoryDep.genreQueryToResultsMap.value = genreResultsMap
            ftsRepositoryDep.playlistQueryToResultsMap.value = playlistResultsMap

            return SearchStateHolder(
                ftsRepository = ftsRepositoryDep,
                playbackManager = playbackManagerDep,
                navController = navControllerDep,
                stateHolderScope = this,
            )
        }

        "handle" -
            {
                "TextChanged updates search results" -
                    {
                        "for tracks" {
                            val queryResults = FixtureProvider.tracks()
                            val results = mapOf("" to emptyList(), QUERY to queryResults)
                            val subject = getSubject(trackResultsMap = results)

                            testStateHolderState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(SearchUserAction.TextChanged(QUERY))

                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Track(TrackRow.State.fromTrack(it))
                                            },
                                    )
                            }
                        }

                        "for artists" {
                            val queryResults = FixtureProvider.artists().map { it.toBasicArtist() }
                            val results = mapOf("" to emptyList(), QUERY to queryResults)
                            val subject = getSubject(artistResultsMap = results)

                            testStateHolderState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(
                                    SearchUserAction.SearchModeChanged(
                                        com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                            .ARTISTS))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .ARTISTS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .ARTISTS,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Artist(ArtistRow.State.fromArtist(it))
                                            },
                                    )
                            }
                        }

                        "for albums" {
                            val queryResults =
                                FixtureProvider.albums().map { it.toAlbumWithArtists() }
                            val results = mapOf("" to emptyList(), QUERY to queryResults)
                            val subject = getSubject(albumResultsMap = results)

                            testStateHolderState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(
                                        com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                            .ALBUMS))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .ALBUMS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .ALBUMS,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Album(AlbumRow.State.fromAlbum(it))
                                            },
                                    )
                            }
                        }

                        "for genres" {
                            val queryResults = FixtureProvider.genres().map { it.toBasicGenre() }
                            val results = mapOf("" to emptyList(), QUERY to queryResults)
                            val subject = getSubject(genreResultsMap = results)

                            testStateHolderState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(
                                        com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                            .GENRES))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .GENRES,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .GENRES,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Genre(GenreRow.State.fromGenre(it))
                                            },
                                    )
                            }
                        }

                        "for playlists" {
                            val queryResults =
                                FixtureProvider.playlists().map { it.toBasicPlaylist() }
                            val results = mapOf("" to emptyList(), QUERY to queryResults)
                            val subject = getSubject(playlistResultsMap = results)

                            testStateHolderState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(
                                        com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                            .PLAYLISTS))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .PLAYLISTS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption =
                                            com.sebastianvm.musicplayer.core.data.fts.SearchMode
                                                .PLAYLISTS,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Playlist(
                                                    PlaylistRow.State.fromPlaylist(it))
                                            },
                                    )
                            }
                        }
                    }
                "SearchResultClicked" -
                    {
                        "for track plays track" {
                            val subject = getSubject()
                            val track = FixtureProvider.track()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Track(TrackRow.State.fromTrack(track))))
                            advanceUntilIdle()
                            playbackManagerDep.playMediaInvocations shouldBe
                                listOf(
                                    FakePlaybackManager.PlayMediaArguments(
                                        mediaGroup = MediaGroup.SingleTrack(track.id),
                                        initialTrackIndex = 0,
                                    ))
                        }

                        "for artist navigates to artist details" {
                            val subject = getSubject()
                            val artist = FixtureProvider.artist()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Artist(ArtistRow.State.fromArtist(artist))))
                            navControllerDep.backStack.last() shouldBe
                                BackStackEntry(
                                    ArtistUiComponent(
                                        arguments = ArtistArguments(artist.id),
                                        navController = navControllerDep,
                                    ),
                                    presentationMode = NavOptions.PresentationMode.Screen,
                                )
                        }

                        "for album navigates to album details" {
                            val subject = getSubject()
                            val album = FixtureProvider.album().toAlbumWithArtists()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Album(AlbumRow.State.fromAlbum(album))))
                            navControllerDep.backStack.last() shouldBe
                                BackStackEntry(
                                    uiComponent =
                                        AlbumDetailsUiComponent(
                                            arguments =
                                                AlbumDetailsArguments(
                                                    albumId = album.id,
                                                    albumName = album.title,
                                                    imageUri = album.imageUri,
                                                    artists =
                                                        album.artists.joinToString { it.name },
                                                ),
                                            navController = navControllerDep,
                                        ),
                                    presentationMode = NavOptions.PresentationMode.Screen,
                                )
                        }

                        "for genre navigates to genre details" {
                            val subject = getSubject()
                            val genre = FixtureProvider.genre()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Genre(GenreRow.State.fromGenre(genre))))
                            navControllerDep.backStack.last() shouldBe
                                BackStackEntry(
                                    GenreDetailsUiComponent(
                                        arguments = GenreDetailsArguments(genre.id, genre.name),
                                        navController = navControllerDep,
                                    ),
                                    presentationMode = NavOptions.PresentationMode.Screen,
                                )
                        }

                        "for playlist navigates to playlist details" {
                            val subject = getSubject()
                            val playlist = FixtureProvider.playlist().toBasicPlaylist()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Playlist(
                                        PlaylistRow.State.fromPlaylist(playlist))))

                            navControllerDep.backStack.last() shouldBe
                                BackStackEntry(
                                    PlaylistDetailsUiComponent(
                                        arguments =
                                            PlaylistDetailsArguments(playlist.id, playlist.name),
                                        navController = navControllerDep,
                                    ),
                                    presentationMode = NavOptions.PresentationMode.Screen,
                                )
                        }
                    }
            }
    })
