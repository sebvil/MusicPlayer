package com.sebastianvm.musicplayer.ui.search

import com.google.common.annotations.VisibleForTesting
import com.ramcosta.composedestinations.spec.Direction
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.destinations.AlbumContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.destinations.GenreContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.PlaylistContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackContextMenuDestination
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class SearchQuery(val term: String, val mode: SearchMode)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<SearchState>, SearchUserAction> {

    private val query =
        MutableStateFlow(SearchQuery(term = "", mode = SearchMode.SONGS))

    private val searchResults = query.debounce(DEBOUNCE_TIME).flatMapLatest { newQuery ->
        when (newQuery.mode) {
            SearchMode.SONGS -> ftsRepository.searchTracks(newQuery.term)
                .map { tracks ->
                    tracks.map {
                        it.toModelListItemState(
                            trailingButtonType = TrailingButtonType.More
                        )
                    }
                }

            SearchMode.ARTISTS -> ftsRepository.searchArtists(newQuery.term)
                .map { artists ->
                    artists.map {
                        it.toModelListItemState(
                            trailingButtonType = TrailingButtonType.More
                        )
                    }
                }

            SearchMode.ALBUMS -> ftsRepository.searchAlbums(newQuery.term)
                .map { albums -> albums.map { it.toModelListItemState() } }

            SearchMode.GENRES -> ftsRepository.searchGenres(newQuery.term)
                .map { genres -> genres.map { it.toModelListItemState() } }

            SearchMode.PLAYLISTS -> ftsRepository.searchPlaylists(newQuery.term)
                .map { playlists -> playlists.map { it.toModelListItemState() } }
        }
    }

    private val playbackResult = MutableStateFlow<PlaybackResult?>(null)
    private val destination = MutableStateFlow<Direction?>(null)

    override val state: StateFlow<UiState<SearchState>> = combine(
        query.map { it.mode },
        searchResults,
        playbackResult,
        destination
    ) { selectedOption, results, playbackResult, destination ->
        Data(
            state = SearchState(
                selectedOption = selectedOption,
                searchResults = results,
                playbackResult = playbackResult,
                navigationState = destination,
            )
        )
    }.stateIn(stateHolderScope, SharingStarted.Eagerly, Loading)

    private fun onTrackSearchResultClicked(trackId: Long) {
        playbackManager.playMedia(MediaGroup.SingleTrack(trackId)).onEach { result ->
            when (result) {
                is PlaybackResult.Loading, is PlaybackResult.Error -> {
                    playbackResult.update { result }
                }

                is PlaybackResult.Success -> {
                    playbackResult.update { null }
                }
            }
        }.launchIn(stateHolderScope)
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        destination.update {
            ArtistRouteDestination(
                ArtistArguments(artistId = artistId)
            )
        }
    }

    private fun onAlbumSearchResultClicked(albumId: Long) {
        destination.update {
            TrackListRouteDestination(
                TrackListArgumentsForNav(trackListType = MediaGroup.Album(albumId = albumId))
            )
        }
    }

    private fun onGenreSearchResultClicked(genreId: Long) {
        destination.update {
            TrackListRouteDestination(
                TrackListArgumentsForNav(trackListType = MediaGroup.Genre(genreId = genreId))
            )
        }
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long) {
        destination.update {
            TrackListRouteDestination(
                TrackListArgumentsForNav(trackListType = MediaGroup.Playlist(playlistId = playlistId))
            )
        }
    }

    private fun onTrackSearchResultOverflowMenuIconClicked(trackId: Long) {
        destination.update {
            TrackContextMenuDestination(
                TrackContextMenuArguments(
                    trackId = trackId,
                    mediaGroup = MediaGroup.SingleTrack(
                        trackId = trackId
                    )
                )
            )
        }
    }

    private fun onArtistSearchResultOverflowMenuIconClicked(artistId: Long) {
        destination.update {
            ArtistContextMenuDestination(
                ArtistContextMenuArguments(artistId = artistId)
            )
        }
    }

    private fun onAlbumSearchResultOverflowMenuIconClicked(albumId: Long) {
        destination.update {
            AlbumContextMenuDestination(
                AlbumContextMenuArguments(albumId = albumId)
            )
        }
    }

    private fun onGenreSearchResultOverflowMenuIconClicked(genreId: Long) {
        destination.update {
            GenreContextMenuDestination(
                GenreContextMenuArguments(genreId = genreId)
            )
        }
    }

    private fun onPlaylistSearchResultOverflowMenuIconClicked(playlistId: Long) {
        destination.update {
            PlaylistContextMenuDestination(
                PlaylistContextMenuArguments(playlistId = playlistId)
            )
        }
    }

    @Suppress("CyclomaticComplexMethod")
    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchResultClicked -> {
                when (action.mediaType) {
                    SearchMode.SONGS -> onTrackSearchResultClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultClicked(action.id)
                    SearchMode.PLAYLISTS -> onPlaylistSearchResultClicked(action.id)
                }
            }

            is SearchUserAction.SearchResultOverflowMenuIconClicked -> {
                when (action.mediaType) {
                    SearchMode.SONGS -> onTrackSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.PLAYLISTS -> onPlaylistSearchResultOverflowMenuIconClicked(action.id)
                }
            }

            is SearchUserAction.SearchModeChanged -> {
                query.update { it.copy(mode = action.newMode) }
            }

            is SearchUserAction.TextChanged -> query.update { it.copy(term = action.newText) }
            is SearchUserAction.DismissPlaybackErrorDialog -> playbackResult.update { null }
            is SearchUserAction.NavigationCompleted -> destination.update { null }
        }
    }

    companion object {
        @VisibleForTesting
        const val DEBOUNCE_TIME = 500L
    }
}

data class SearchState(
    val selectedOption: SearchMode,
    val searchResults: List<ModelListItemState>,
    val playbackResult: PlaybackResult?,
    val navigationState: Direction?
) : State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val id: Long, val mediaType: SearchMode) : SearchUserAction
    data class SearchResultOverflowMenuIconClicked(val id: Long, val mediaType: SearchMode) :
        SearchUserAction

    data class TextChanged(val newText: String) : SearchUserAction
    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
    data object DismissPlaybackErrorDialog : SearchUserAction
    data object NavigationCompleted : SearchUserAction
}
