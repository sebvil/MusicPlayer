package com.sebastianvm.musicplayer.features.search

import android.app.SearchManager.QUERY
import com.sebastianvm.musicplayer.core.commontest.FixtureProvider
import com.sebastianvm.musicplayer.core.commontest.extensions.advanceUntilIdle
import com.sebastianvm.musicplayer.core.commontest.extensions.testViewModelState
import com.sebastianvm.musicplayer.core.data.fts.SearchMode
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
import com.sebastianvm.musicplayer.core.servicestest.playback.FakePlaybackManager
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeBackstackEntry
import com.sebastianvm.musicplayer.core.uitest.navigation.FakeNavController
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.search.SearchProps
import com.sebastianvm.musicplayer.features.test.FakeFeatures
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow

class SearchViewModelTest :
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
        ): SearchViewModel {
            ftsRepositoryDep.trackQueryToResultsMap.value = trackResultsMap
            ftsRepositoryDep.artistQueryToResultsMap.value = artistResultsMap
            ftsRepositoryDep.albumQueryToResultsMap.value = albumResultsMap
            ftsRepositoryDep.genreQueryToResultsMap.value = genreResultsMap
            ftsRepositoryDep.playlistQueryToResultsMap.value = playlistResultsMap

            return SearchViewModel(
                searchRepository = ftsRepositoryDep,
                playbackManager = playbackManagerDep,
                props = MutableStateFlow(SearchProps(navController = navControllerDep)),
                features = FakeFeatures(),
                vmScope = this,
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

                            testViewModelState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(SearchUserAction.TextChanged(QUERY))

                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
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

                            testViewModelState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(
                                    SearchUserAction.SearchModeChanged(SearchMode.ARTISTS)
                                )
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.ARTISTS,
                                        searchResults = emptyList(),
                                    )
                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.ARTISTS,
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

                            testViewModelState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(SearchMode.ALBUMS)
                                )
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.ALBUMS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.ALBUMS,
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

                            testViewModelState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(SearchMode.GENRES)
                                )
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.GENRES,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.GENRES,
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

                            testViewModelState(subject) {
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.TRACKS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(
                                    SearchUserAction.SearchModeChanged(SearchMode.PLAYLISTS)
                                )
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.PLAYLISTS,
                                        searchResults = emptyList(),
                                    )

                                subject.handle(SearchUserAction.TextChanged(QUERY))
                                awaitItem() shouldBe
                                    SearchState(
                                        selectedOption = SearchMode.PLAYLISTS,
                                        searchResults =
                                            queryResults.map {
                                                SearchResult.Playlist(
                                                    PlaylistRow.State.fromPlaylist(it)
                                                )
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
                                    SearchResult.Track(TrackRow.State.fromTrack(track))
                                )
                            )
                            advanceUntilIdle()
                            playbackManagerDep.playMediaInvocations shouldBe
                                listOf(
                                    FakePlaybackManager.PlayMediaArguments(
                                        mediaGroup = MediaGroup.SingleTrack(track.id),
                                        initialTrackIndex = 0,
                                    )
                                )
                        }

                        "for artist navigates to artist details" {
                            val subject = getSubject()
                            val artist = FixtureProvider.artist()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Artist(ArtistRow.State.fromArtist(artist))
                                )
                            )
                            navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    FakeMvvmComponent(
                                        arguments = ArtistDetailsArguments(artist.id)
                                    ),
                                    navOptions =
                                        NavOptions(
                                            presentationMode = NavOptions.PresentationMode.Screen
                                        ),
                                )
                        }

                        "for album navigates to album details" {
                            val subject = getSubject()
                            val album = FixtureProvider.album().toAlbumWithArtists()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Album(AlbumRow.State.fromAlbum(album))
                                )
                            )
                            navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    mvvmComponent =
                                        FakeMvvmComponent(
                                            arguments =
                                                AlbumDetailsArguments(
                                                    albumId = album.id,
                                                    albumName = album.title,
                                                    imageUri = album.imageUri,
                                                    artists = album.artists.joinToString { it.name },
                                                )
                                        ),
                                    navOptions =
                                        NavOptions(
                                            presentationMode = NavOptions.PresentationMode.Screen
                                        ),
                                )
                        }

                        "for genre navigates to genre details" {
                            val subject = getSubject()
                            val genre = FixtureProvider.genre()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Genre(GenreRow.State.fromGenre(genre))
                                )
                            )
                            navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    FakeMvvmComponent(
                                        arguments = GenreDetailsArguments(genre.id, genre.name)
                                    ),
                                    navOptions =
                                        NavOptions(
                                            presentationMode = NavOptions.PresentationMode.Screen
                                        ),
                                )
                        }

                        "for playlist navigates to playlist details" {
                            val subject = getSubject()
                            val playlist = FixtureProvider.playlist().toBasicPlaylist()
                            subject.handle(
                                SearchUserAction.SearchResultClicked(
                                    SearchResult.Playlist(PlaylistRow.State.fromPlaylist(playlist))
                                )
                            )

                            navControllerDep.backStack.last() shouldBe
                                FakeBackstackEntry(
                                    FakeMvvmComponent(
                                        arguments =
                                            PlaylistDetailsArguments(playlist.id, playlist.name)
                                    ),
                                    navOptions =
                                        NavOptions(
                                            presentationMode = NavOptions.PresentationMode.Screen
                                        ),
                                )
                        }
                    }
            }
    })
