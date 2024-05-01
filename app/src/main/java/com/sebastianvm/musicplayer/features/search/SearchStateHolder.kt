package com.sebastianvm.musicplayer.features.search

import androidx.compose.runtime.Composable
import com.google.common.annotations.VisibleForTesting
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuStateHolder
import com.sebastianvm.musicplayer.features.album.menu.albumContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.artist.menu.artistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuStateHolder
import com.sebastianvm.musicplayer.features.genre.menu.genreContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.playlist.menu.playlistContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.features.track.menu.SourceTrackList
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuStateHolder
import com.sebastianvm.musicplayer.features.track.menu.trackContextMenuStateHolderFactory
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.fts.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
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


data class SearchState(
    val selectedOption: SearchMode,
    val searchResults: List<ModelListItemState>,
    val playbackResult: PlaybackResult?,
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

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val albumContextMenuStateHolderFactory: StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuStateHolder>,
    private val artistContextMenuStateHolderFactory: StateHolderFactory<ArtistContextMenuArguments, ArtistContextMenuStateHolder>,
    private val genreContextMenuStateHolderFactory: StateHolderFactory<GenreContextMenuArguments, GenreContextMenuStateHolder>,
    private val trackContextMenuStateHolderFactory: StateHolderFactory<TrackContextMenuArguments, TrackContextMenuStateHolder>,
    private val playlistContextMenuStateHolderFactory: StateHolderFactory<PlaylistContextMenuArguments, PlaylistContextMenuStateHolder>,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<SearchState, SearchUserAction> {

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

    override val state: StateFlow<SearchState> = combine(
        query.map { it.mode },
        searchResults,
        playbackResult,
        contextMenuObjectId
    ) { selectedOption, results, playbackResult, contextMenuObjectIds ->
        SearchState(
            selectedOption = selectedOption,
            searchResults = results,
            playbackResult = playbackResult,
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
    }.stateIn(
        stateHolderScope, SharingStarted.Lazily, SearchState(
            selectedOption = SearchMode.SONGS,
            searchResults = emptyList(),
            playbackResult = null,
            albumContextMenuStateHolder = null,
            artistContextMenuStateHolder = null,
            genreContextMenuStateHolder = null,
            playlistContextMenuStateHolder = null,
            trackContextMenuStateHolder = null
        )
    )

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
//        destination.update {
//            ArtistRouteDestination(
//                ArtistArguments(artistId = artistId)
//            )
//        }
        TODO("navigation")
    }

    private fun onAlbumSearchResultClicked(albumId: Long) {
        TODO("navigation")
//        destination.update {
//            TrackListRouteDestination(
//                TrackListArgumentsForNav(trackListType = MediaGroup.Album(albumId = albumId))
//            )
//        }
    }

    private fun onGenreSearchResultClicked(genreId: Long) {
        TODO("navigation")
//        destination.update {
//            TrackListRouteDestination(
//                TrackListArgumentsForNav(trackListType = MediaGroup.Genre(genreId = genreId))
//            )
//        }
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long) {
        TODO("navigation")
//        destination.update {
//            TrackListRouteDestination(
//                TrackListArgumentsForNav(trackListType = MediaGroup.Playlist(playlistId = playlistId))
//            )
//        }
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
            is SearchUserAction.NavigationCompleted -> {
//                destination.update { null }
            }

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


@Composable
fun rememberSearchStateHolder(): SearchStateHolder {
    val albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory()
    val artistContextMenuStateHolderFactory = artistContextMenuStateHolderFactory()
    val genreContextMenuStateHolderFactory = genreContextMenuStateHolderFactory()
    val trackContextMenuStateHolderFactory = trackContextMenuStateHolderFactory()
    val playlistContextMenuStateHolderFactory = playlistContextMenuStateHolderFactory()

    return stateHolder { dependencyContainer ->
        SearchStateHolder(
            ftsRepository = dependencyContainer.repositoryProvider.searchRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            albumContextMenuStateHolderFactory = albumContextMenuStateHolderFactory,
            artistContextMenuStateHolderFactory = artistContextMenuStateHolderFactory,
            genreContextMenuStateHolderFactory = genreContextMenuStateHolderFactory,
            trackContextMenuStateHolderFactory = trackContextMenuStateHolderFactory,
            playlistContextMenuStateHolderFactory = playlistContextMenuStateHolderFactory,
        )
    }
}
