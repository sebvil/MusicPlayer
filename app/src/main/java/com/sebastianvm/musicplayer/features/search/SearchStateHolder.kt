package com.sebastianvm.musicplayer.features.search

import com.sebastianvm.musicplayer.core.data.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.ArtistRow
import com.sebastianvm.musicplayer.core.designsystems.components.GenreRow
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.TrackRow
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.featues.album.details.AlbumDetailsUiComponent
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistUiComponent
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.genre.details.GenreDetailsUiComponent
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsUiComponent
import com.sebastianvm.musicplayer.services.Services
import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.services.features.mvvm.State
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.mvvm.UserAction
import com.sebastianvm.musicplayer.services.features.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.playback.PlaybackManager
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

data class SearchQuery(
    val term: String,
    val mode: com.sebastianvm.musicplayer.core.data.fts.SearchMode,
)

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

data class SearchState(
    val selectedOption: com.sebastianvm.musicplayer.core.data.fts.SearchMode,
    val searchResults: List<SearchResult<*>>,
) : State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val result: SearchResult<*>) : SearchUserAction

    data class TextChanged(val newText: String) : SearchUserAction

    data class SearchModeChanged(
        val newMode: com.sebastianvm.musicplayer.core.data.fts.SearchMode
    ) : SearchUserAction
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<SearchState, SearchUserAction> {

    private val query =
        MutableStateFlow(
            SearchQuery(
                term = "",
                mode = com.sebastianvm.musicplayer.core.data.fts.SearchMode.TRACKS,
            ))

    private val searchResults =
        query.debounce(DEBOUNCE_TIME).flatMapLatest { newQuery ->
            when (newQuery.mode) {
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.TRACKS ->
                    ftsRepository.searchTracks(newQuery.term).map { tracks ->
                        tracks.map { SearchResult.Track(TrackRow.State.fromTrack(it)) }
                    }
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.ARTISTS ->
                    ftsRepository.searchArtists(newQuery.term).map { artists ->
                        artists.map { SearchResult.Artist(ArtistRow.State.fromArtist(it)) }
                    }
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.ALBUMS ->
                    ftsRepository.searchAlbums(newQuery.term).map { albums ->
                        albums.map { SearchResult.Album(AlbumRow.State.fromAlbum(it)) }
                    }
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.GENRES ->
                    ftsRepository.searchGenres(newQuery.term).map { genres ->
                        genres.map { SearchResult.Genre(GenreRow.State.fromGenre(it)) }
                    }
                com.sebastianvm.musicplayer.core.data.fts.SearchMode.PLAYLISTS ->
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
                    SearchState(
                        selectedOption =
                            com.sebastianvm.musicplayer.core.data.fts.SearchMode.TRACKS,
                        searchResults = emptyList(),
                    ),
            )

    private fun onTrackSearchResultClicked(trackId: Long) {
        stateHolderScope.launch { playbackManager.playMedia(MediaGroup.SingleTrack(trackId)) }
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        navController.push(ArtistUiComponent(ArtistArguments(artistId), navController))
    }

    private fun onAlbumSearchResultClicked(albumItem: AlbumRow.State) {
        navController.push(
            AlbumDetailsUiComponent(
                AlbumDetailsArguments(
                    albumId = albumItem.id,
                    albumName = albumItem.albumName,
                    imageUri = albumItem.artworkUri,
                    artists = albumItem.artists,
                ),
                navController,
            ))
    }

    private fun onGenreSearchResultClicked(genreId: Long, genreName: String) {
        navController.push(
            GenreDetailsUiComponent(GenreDetailsArguments(genreId, genreName), navController))
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long, playlistName: String) {
        navController.push(
            PlaylistDetailsUiComponent(
                PlaylistDetailsArguments(playlistId = playlistId, playlistName = playlistName),
                navController,
            ))
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

fun getSearchStateHolder(
    services: Services,
    navController: NavController,
): SearchStateHolder {
    return SearchStateHolder(
        ftsRepository = services.repositoryProvider.searchRepository,
        playbackManager = services.playbackManager,
        navController = navController,
    )
}
