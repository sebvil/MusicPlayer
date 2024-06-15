package com.sebastianvm.musicplayer.features.search

import com.google.common.annotations.VisibleForTesting
import com.sebastianvm.musicplayer.designsystem.components.AlbumRow
import com.sebastianvm.musicplayer.designsystem.components.ArtistRow
import com.sebastianvm.musicplayer.designsystem.components.GenreRow
import com.sebastianvm.musicplayer.designsystem.components.PlaylistRow
import com.sebastianvm.musicplayer.designsystem.components.TrackRow
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchQuery(val term: String, val mode: SearchMode)

sealed interface SearchResult<T> {
    val state: T
    val id: Long

    data class Track(override val state: TrackRow.State) : SearchResult<TrackRow.State> {
        override val id: Long = state.id
    }

    data class Artist(override val state: ArtistRow.State) : SearchResult<ArtistRow.State> {
        override val id: Long = state.id
    }

    data class Album(override val state: AlbumRow.State) : SearchResult<AlbumRow.State> {
        override val id: Long = state.id
    }

    data class Genre(override val state: GenreRow.State) : SearchResult<GenreRow.State> {
        override val id: Long = state.id
    }

    data class Playlist(override val state: PlaylistRow.State) : SearchResult<PlaylistRow.State> {
        override val id: Long = state.id
    }
}

data class SearchState(val selectedOption: SearchMode, val searchResults: List<SearchResult<*>>) :
    State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val result: SearchResult<*>) : SearchUserAction

    data class TextChanged(val newText: String) : SearchUserAction

    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<SearchState, SearchUserAction> {

    private val query = MutableStateFlow(SearchQuery(term = "", mode = SearchMode.TRACKS))

    private val searchResults =
        query.debounce(DEBOUNCE_TIME).flatMapLatest { newQuery ->
            when (newQuery.mode) {
                SearchMode.TRACKS ->
                    ftsRepository.searchTracks(newQuery.term).map { tracks ->
                        tracks.map { SearchResult.Track(TrackRow.State.fromTrack(it)) }
                    }
                SearchMode.ARTISTS ->
                    ftsRepository.searchArtists(newQuery.term).map { artists ->
                        artists.map { SearchResult.Artist(ArtistRow.State.fromArtist(it)) }
                    }
                SearchMode.ALBUMS ->
                    ftsRepository.searchAlbums(newQuery.term).map { albums ->
                        albums.map { SearchResult.Album(AlbumRow.State.fromAlbum(it)) }
                    }
                SearchMode.GENRES ->
                    ftsRepository.searchGenres(newQuery.term).map { genres ->
                        genres.map { SearchResult.Genre(GenreRow.State.fromGenre(it)) }
                    }
                SearchMode.PLAYLISTS ->
                    ftsRepository.searchPlaylists(newQuery.term).map { playlists ->
                        playlists.map { SearchResult.Playlist(PlaylistRow.State.fromPlaylist(it)) }
                    }
            }
        }

    override val state: StateFlow<SearchState> =
        combine(query.map { it.mode }, searchResults) { selectedOption, results ->
                SearchState(selectedOption = selectedOption, searchResults = results)
            }
            .stateIn(
                scope = stateHolderScope,
                started = SharingStarted.Lazily,
                initialValue =
                    SearchState(selectedOption = SearchMode.TRACKS, searchResults = emptyList()),
            )

    private fun onTrackSearchResultClicked(trackId: Long) {
        stateHolderScope.launch { playbackManager.playMedia(MediaGroup.SingleTrack(trackId)) }
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        navController.push(ArtistUiComponent(ArtistArguments(artistId), navController))
    }

    private fun onAlbumSearchResultClicked(albumId: Long, albumName: String, imageUri: String) {
        navController.push(
            AlbumDetailsUiComponent(
                AlbumDetailsArguments(
                    albumId = albumId,
                    albumName = albumName,
                    imageUri = imageUri,
                ),
                navController,
            )
        )
    }

    private fun onGenreSearchResultClicked(genreId: Long, genreName: String) {
        navController.push(
            GenreDetailsUiComponent(GenreDetailsArguments(genreId, genreName), navController)
        )
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long, playlistName: String) {
        navController.push(
            PlaylistDetailsUiComponent(
                PlaylistDetailsArguments(playlistId = playlistId, playlistName = playlistName),
                navController,
            )
        )
    }

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchResultClicked -> {
                when (val result = action.result) {
                    is SearchResult.Track -> onTrackSearchResultClicked(result.id)
                    is SearchResult.Artist -> onArtistSearchResultClicked(result.id)
                    is SearchResult.Album ->
                        onAlbumSearchResultClicked(
                            albumId = result.id,
                            albumName = result.state.albumName,
                            imageUri = result.state.artworkUri,
                        )
                    is SearchResult.Genre ->
                        onGenreSearchResultClicked(result.id, result.state.genreName)
                    is SearchResult.Playlist ->
                        onPlaylistSearchResultClicked(result.id, result.state.playlistName)
                }
            }
            is SearchUserAction.SearchModeChanged -> {
                query.update { it.copy(mode = action.newMode) }
            }
            is SearchUserAction.TextChanged -> query.update { it.copy(term = action.newText) }
        }
    }

    companion object {
        @VisibleForTesting const val DEBOUNCE_TIME = 500L
    }
}

fun getSearchStateHolder(
    dependencies: Dependencies,
    navController: NavController,
): SearchStateHolder {
    return SearchStateHolder(
        ftsRepository = dependencies.repositoryProvider.searchRepository,
        playbackManager = dependencies.repositoryProvider.playbackManager,
        navController = navController,
    )
}
