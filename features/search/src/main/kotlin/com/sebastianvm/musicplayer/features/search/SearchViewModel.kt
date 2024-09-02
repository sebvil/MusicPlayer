package com.sebastianvm.musicplayer.features.search

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.data.fts.SearchMode
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.albumDetails
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.artistDetails
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.genreDetails
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.playlistDetails
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
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
class SearchViewModel(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    vmScope: CoroutineScope = getViewModelScope(),
    private val features: FeatureRegistry,
) : BaseViewModel<SearchState, SearchUserAction>(viewModelScope = vmScope) {

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
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue =
                SearchState(selectedOption = SearchMode.TRACKS, searchResults = emptyList()),
            )

    private fun onTrackSearchResultClicked(trackId: Long) {
        viewModelScope.launch { playbackManager.playMedia(MediaGroup.SingleTrack(trackId)) }
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        navController.push(
            features
                .artistDetails()
                .artistDetailsUiComponent(
                    arguments = ArtistDetailsArguments(artistId),
                    navController = navController,
                )
        )
    }

    private fun onAlbumSearchResultClicked(albumItem: AlbumRow.State) {
        navController.push(
            features
                .albumDetails()
                .albumDetailsUiComponent(
                    arguments =
                    AlbumDetailsArguments(
                        albumId = albumItem.id,
                        albumName = albumItem.albumName,
                        imageUri = albumItem.artworkUri,
                        artists = albumItem.artists,
                    ),
                    navController = navController,
                )
        )
    }

    private fun onGenreSearchResultClicked(genreId: Long, genreName: String) {
        navController.push(
            features
                .genreDetails()
                .genreDetailsUiComponent(
                    GenreDetailsArguments(genreId = genreId, genreName = genreName),
                    navController,
                )
        )
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long, playlistName: String) {
        navController.push(
            features
                .playlistDetails()
                .playlistDetailsUiComponent(
                    arguments =
                    PlaylistDetailsArguments(
                        playlistId = playlistId,
                        playlistName = playlistName,
                    ),
                    navController = navController,
                )
        )
    }

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchResultClicked -> {
                when (val result = action.result) {
                    is SearchResult.Track -> onTrackSearchResultClicked(result.id)
                    is SearchResult.Artist -> onArtistSearchResultClicked(result.id)
                    is SearchResult.Album -> onAlbumSearchResultClicked(result.state)
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
        private const val DEBOUNCE_TIME = 500L
    }
}
