package com.sebastianvm.musicplayer.ui.search

import com.google.common.annotations.VisibleForTesting
import com.ramcosta.composedestinations.spec.Direction
import com.sebastianvm.musicplayer.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolder
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.list.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.features.track.menu.SourceTrackList
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolder
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
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

data class ContextMenuObjectIds(
    val albumId: Long? = null,
    val artistId: Long? = null,
    val genreId: Long? = null,
    val trackId: Long? = null,
    val playlistId: Long? = null
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val albumContextMenuStateHolderFactory: AlbumContextMenuStateHolderFactory,
    private val artistContextMenuStateHolderFactory: ArtistContextMenuStateHolderFactory,
    private val genreContextMenuStateHolderFactory: GenreContextMenuStateHolderFactory,
    private val trackContextMenuStateHolderFactory: TrackContextMenuStateHolderFactory,
    private val playlistContextMenuStateHolderFactory: PlaylistContextMenuStateHolderFactory,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<SearchState>, SearchUserAction> {

    private val query =
        MutableStateFlow(SearchQuery(term = "", mode = SearchMode.SONGS))

    private val contextMenuObjectId: MutableStateFlow<ContextMenuObjectIds> = MutableStateFlow(
        ContextMenuObjectIds()
    )

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
        destination,
        contextMenuObjectId
    ) { selectedOption, results, playbackResult, destination, contextMenuObjectIds ->
        Data(
            state = SearchState(
                selectedOption = selectedOption,
                searchResults = results,
                playbackResult = playbackResult,
                navigationState = destination,
                albumContextMenuStateHolder = contextMenuObjectIds.albumId?.let { albumId ->
                    albumContextMenuStateHolderFactory.getStateHolder(
                        AlbumContextMenuArguments(
                            albumId = albumId
                        )
                    )
                },
                artistContextMenuStateHolder = contextMenuObjectIds.artistId?.let { artistId ->
                    artistContextMenuStateHolderFactory.getStateHolder(
                        ArtistContextMenuArguments(
                            artistId = artistId
                        )
                    )
                },
                genreContextMenuStateHolder = contextMenuObjectIds.genreId?.let { genreId ->
                    genreContextMenuStateHolderFactory.getStateHolder(
                        GenreContextMenuArguments(
                            genreId = genreId
                        )
                    )
                },
                playlistContextMenuStateHolder = contextMenuObjectIds.playlistId?.let { playlistId ->
                    playlistContextMenuStateHolderFactory.getStateHolder(
                        PlaylistContextMenuArguments(
                            playlistId = playlistId
                        )
                    )
                },
                trackContextMenuStateHolder = contextMenuObjectIds.trackId?.let { trackId ->
                    trackContextMenuStateHolderFactory.getStateHolder(
                        TrackContextMenuArguments(
                            trackId = trackId,
                            trackList = SourceTrackList.SearchResults
                        )
                    )
                }

            )
        )
    }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

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
        contextMenuObjectId.update { it.copy(trackId = trackId) }
    }

    private fun onArtistSearchResultOverflowMenuIconClicked(artistId: Long) {
        contextMenuObjectId.update { it.copy(artistId = artistId) }
    }

    private fun onAlbumSearchResultOverflowMenuIconClicked(albumId: Long) {
        contextMenuObjectId.update { it.copy(albumId = albumId) }
    }

    private fun onGenreSearchResultOverflowMenuIconClicked(genreId: Long) {
        contextMenuObjectId.update { it.copy(genreId = genreId) }
    }

    private fun onPlaylistSearchResultOverflowMenuIconClicked(playlistId: Long) {
        contextMenuObjectId.update { it.copy(playlistId = playlistId) }
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
            is SearchUserAction.AlbumContextMenuDismissed -> {
                contextMenuObjectId.update {
                    it.copy(
                        albumId = null
                    )
                }
            }

            is SearchUserAction.ArtistContextMenuDismissed -> {
                contextMenuObjectId.update {
                    it.copy(
                        artistId = null
                    )
                }
            }

            is SearchUserAction.GenreContextMenuDismissed -> {
                contextMenuObjectId.update {
                    it.copy(
                        genreId = null
                    )
                }
            }

            is SearchUserAction.PlaylistContextMenuDismissed -> {
                contextMenuObjectId.update {
                    it.copy(
                        playlistId = null
                    )
                }
            }

            is SearchUserAction.TrackContextMenuDismissed -> {
                contextMenuObjectId.update {
                    it.copy(
                        trackId = null
                    )
                }
            }
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
    val navigationState: Direction?,
    val albumContextMenuStateHolder: AlbumContextMenuStateHolder?,
    val artistContextMenuStateHolder: ArtistContextMenuStateHolder?,
    val genreContextMenuStateHolder: GenreContextMenuStateHolder?,
    val playlistContextMenuStateHolder: PlaylistContextMenuStateHolder?,
    val trackContextMenuStateHolder: TrackContextMenuStateHolder?,
) : State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val id: Long, val mediaType: SearchMode) : SearchUserAction
    data class SearchResultOverflowMenuIconClicked(val id: Long, val mediaType: SearchMode) :
        SearchUserAction

    data object AlbumContextMenuDismissed : SearchUserAction
    data object ArtistContextMenuDismissed : SearchUserAction
    data object GenreContextMenuDismissed : SearchUserAction
    data object PlaylistContextMenuDismissed : SearchUserAction
    data object TrackContextMenuDismissed : SearchUserAction
    data class TextChanged(val newText: String) : SearchUserAction
    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
    data object DismissPlaybackErrorDialog : SearchUserAction
    data object NavigationCompleted : SearchUserAction
}
