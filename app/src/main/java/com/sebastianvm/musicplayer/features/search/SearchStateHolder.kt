package com.sebastianvm.musicplayer.features.search

import androidx.compose.runtime.Composable
import com.google.common.annotations.VisibleForTesting
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenu
import com.sebastianvm.musicplayer.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenu
import com.sebastianvm.musicplayer.features.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.features.artist.screen.ArtistScreen
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenu
import com.sebastianvm.musicplayer.features.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenu
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.menu.SourceTrackList
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenu
import com.sebastianvm.musicplayer.features.track.menu.TrackContextMenuArguments
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
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.rememberStateHolder
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

data class SearchState(
    val selectedOption: SearchMode,
    val searchResults: List<ModelListItemState>,
    val playbackResult: PlaybackResult?,
) : State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val id: Long, val mediaType: SearchMode) : SearchUserAction
    data class SearchResultOverflowMenuIconClicked(val id: Long, val mediaType: SearchMode) :
        SearchUserAction

    data class TextChanged(val newText: String) : SearchUserAction
    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
    data object DismissPlaybackErrorDialog : SearchUserAction
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchStateHolder(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    private val navController: NavController,
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<SearchState, SearchUserAction> {

    private val query = MutableStateFlow(SearchQuery(term = "", mode = SearchMode.SONGS))

    private val searchResults = query.debounce(DEBOUNCE_TIME).flatMapLatest { newQuery ->
        when (newQuery.mode) {
            SearchMode.SONGS -> ftsRepository.searchTracks(newQuery.term).map { tracks ->
                tracks.map {
                    it.toModelListItemState(
                        trailingButtonType = TrailingButtonType.More
                    )
                }
            }

            SearchMode.ARTISTS -> ftsRepository.searchArtists(newQuery.term).map { artists ->
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
        playbackResult
    ) { selectedOption, results, playbackResult ->
        SearchState(
            selectedOption = selectedOption,
            searchResults = results,
            playbackResult = playbackResult,
        )
    }.stateIn(
        scope = stateHolderScope,
        started = SharingStarted.Lazily,
        initialValue = SearchState(
            selectedOption = SearchMode.SONGS,
            searchResults = emptyList(),
            playbackResult = null,
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
        navController.push(ArtistScreen(ArtistArguments(artistId), navController))
    }

    private fun onAlbumSearchResultClicked(albumId: Long) {
        navController.push(TrackList(TrackListArguments(MediaGroup.Album(albumId)), navController))
    }

    private fun onGenreSearchResultClicked(genreId: Long) {
        navController.push(TrackList(TrackListArguments(MediaGroup.Genre(genreId)), navController))
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long) {
        navController.push(
            TrackList(
                TrackListArguments(MediaGroup.Playlist(playlistId)),
                navController
            )
        )
    }

    private fun onTrackSearchResultOverflowMenuIconClicked(trackId: Long) {
        navController.push(
            TrackContextMenu(
                arguments = TrackContextMenuArguments(
                    trackId = trackId,
                    trackList = SourceTrackList.SearchResults
                ),
                navController = navController
            ),
            navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
        )
    }

    private fun onArtistSearchResultOverflowMenuIconClicked(artistId: Long) {
        navController.push(
            ArtistContextMenu(
                arguments = ArtistContextMenuArguments(artistId = artistId),
                navController = navController
            ),
            navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
        )
    }

    private fun onAlbumSearchResultOverflowMenuIconClicked(albumId: Long) {
        navController.push(
            AlbumContextMenu(
                arguments = AlbumContextMenuArguments(albumId = albumId),
                navController = navController
            ),
            navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
        )
    }

    private fun onGenreSearchResultOverflowMenuIconClicked(genreId: Long) {
        navController.push(
            GenreContextMenu(
                arguments = GenreContextMenuArguments(genreId = genreId),
                navController = navController
            ),
            navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
        )
    }

    private fun onPlaylistSearchResultOverflowMenuIconClicked(playlistId: Long) {
        navController.push(
            PlaylistContextMenu(
                arguments = PlaylistContextMenuArguments(playlistId = playlistId),
                navController = navController
            ),
            navOptions = NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet)
        )
    }

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
        }
    }

    companion object {
        @VisibleForTesting
        const val DEBOUNCE_TIME = 500L
    }
}

@Composable
fun rememberSearchStateHolder(navController: NavController): SearchStateHolder {
    return rememberStateHolder { dependencyContainer ->
        SearchStateHolder(
            ftsRepository = dependencyContainer.repositoryProvider.searchRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            navController = navController,
        )
    }
}
