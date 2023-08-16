package com.sebastianvm.musicplayer.ui.search

import androidx.lifecycle.viewModelScope
import com.google.common.annotations.VisibleForTesting
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
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchQueryState(val term: String, val mode: SearchMode)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<SearchState, SearchUserAction>() {

    override val defaultState: SearchState by lazy {
        SearchState(
            selectedOption = SearchMode.SONGS,
            searchResults = listOf(),
            playbackResult = null
        )
    }

    private val selectedOption: SearchMode
        get() = (state as? Data)?.state?.selectedOption ?: SearchMode.SONGS
    private val query =
        MutableStateFlow(SearchQueryState(term = "", mode = selectedOption))

    init {
        setDataState { it }
        query.debounce(DEBOUNCE_TIME).flatMapLatest { newQuery ->
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
        }.flowOn(defaultDispatcher).onEach { results ->
            setDataState {
                it.copy(searchResults = results)
            }
        }.launchIn(viewModelScope)
    }

    private fun onTrackSearchResultClicked(trackId: Long) {
        playbackManager.playSingleTrack(trackId).onEach { result ->
            when (result) {
                is PlaybackResult.Loading, is PlaybackResult.Error -> {
                    setDataState {
                        it.copy(
                            playbackResult = result
                        )
                    }
                }

                is PlaybackResult.Success -> {
                    setDataState { it.copy(playbackResult = null) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                ArtistRouteDestination(
                    ArtistArguments(artistId = artistId)
                )
            )
        )
    }

    private fun onAlbumSearchResultClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                TrackListRouteDestination(
                    TrackListArgumentsForNav(trackListType = MediaGroup.Album(albumId = albumId))
                )
            )
        )
    }

    private fun onGenreSearchResultClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                TrackListRouteDestination(
                    TrackListArgumentsForNav(trackListType = MediaGroup.Genre(genreId = genreId))
                )
            )
        )
    }

    private fun onPlaylistSearchResultClicked(playlistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                TrackListRouteDestination(
                    TrackListArgumentsForNav(trackListType = MediaGroup.Playlist(playlistId = playlistId))
                )
            )
        )
    }

    private fun onTrackSearchResultOverflowMenuIconClicked(trackId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                TrackContextMenuDestination(
                    TrackContextMenuArguments(
                        trackId = trackId,
                        mediaGroup = MediaGroup.SingleTrack(
                            trackId = trackId
                        )
                    )
                )
            )
        )
    }

    private fun onArtistSearchResultOverflowMenuIconClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                ArtistContextMenuDestination(
                    ArtistContextMenuArguments(artistId = artistId)
                )
            )
        )
    }

    private fun onAlbumSearchResultOverflowMenuIconClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                AlbumContextMenuDestination(
                    AlbumContextMenuArguments(albumId = albumId)
                )
            )
        )
    }

    private fun onGenreSearchResultOverflowMenuIconClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                GenreContextMenuDestination(
                    GenreContextMenuArguments(genreId = genreId)
                )
            )
        )
    }

    private fun onPlaylistSearchResultOverflowMenuIconClicked(playlistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                PlaylistContextMenuDestination(
                    PlaylistContextMenuArguments(playlistId = playlistId)
                )
            )
        )
    }

    @Suppress("CyclomaticComplexMethod")
    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchResultClicked -> {
                when (selectedOption) {
                    SearchMode.SONGS -> onTrackSearchResultClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultClicked(action.id)
                    SearchMode.PLAYLISTS -> onPlaylistSearchResultClicked(action.id)
                }
            }

            is SearchUserAction.SearchResultOverflowMenuIconClicked -> {
                when (selectedOption) {
                    SearchMode.SONGS -> onTrackSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.PLAYLISTS -> onPlaylistSearchResultOverflowMenuIconClicked(action.id)
                }
            }

            is SearchUserAction.SearchModeChanged -> {
                setDataState { it.copy(selectedOption = action.newMode) }
                query.update { it.copy(mode = action.newMode) }
            }

            is SearchUserAction.TextChanged -> query.update { it.copy(term = action.newText) }
            is SearchUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is SearchUserAction.DismissPlaybackErrorDialog -> setDataState { it.copy(playbackResult = null) }
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
    val playbackResult: PlaybackResult? = null
) : State

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val id: Long) : SearchUserAction
    data class SearchResultOverflowMenuIconClicked(val id: Long) : SearchUserAction
    data class TextChanged(val newText: String) : SearchUserAction
    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
    data object UpButtonClicked : SearchUserAction
    data object DismissPlaybackErrorDialog : SearchUserAction
}
