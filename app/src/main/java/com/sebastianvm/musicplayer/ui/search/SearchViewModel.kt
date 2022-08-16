package com.sebastianvm.musicplayer.ui.search

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.SearchMode
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.GenreContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
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
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchQueryState(val term: String, val mode: SearchMode)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    initialState: SearchState,
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel<SearchUiEvent, SearchState>(initialState),
    ViewModelInterface<SearchState, SearchUserAction> {

    private val query =
        MutableStateFlow(SearchQueryState(term = "", mode = initialState.selectedOption))


    init {
        query.debounce(50).flatMapLatest { newQuery ->
            when (newQuery.mode) {
                SearchMode.SONGS -> ftsRepository.searchTracks(newQuery.term)
                    .map { tracks -> tracks.map { it.track.toModelListItemState() } }
                SearchMode.ARTISTS -> ftsRepository.searchArtists(newQuery.term)
                    .map { artists -> artists.map { it.toModelListItemState() } }
                SearchMode.ALBUMS -> ftsRepository.searchAlbums(newQuery.term)
                    .map { albums -> albums.map { it.album.toModelListItemState() } }
                SearchMode.GENRES -> ftsRepository.searchGenres(newQuery.term)
                    .map { genres -> genres.map { it.toModelListItemState() } }
            }
        }.flowOn(defaultDispatcher).onEach {
            setState {
                copy(searchResults = it)
            }
        }.launchIn(viewModelScope)
    }

    private fun onTrackSearchResultClicked(trackId: Long) {
        viewModelScope.launch {
            playbackManager.playSingleTrack(trackId)
            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
        }
    }

    private fun onArtistSearchResultClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.Artist(
                    ArtistArguments(artistId = artistId)
                )
            )
        )
    }

    private fun onAlbumSearchResultClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.Album(
                    AlbumArguments(albumId = albumId)
                )
            )
        )
    }

    private fun onGenreSearchResultClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackList(
                    TrackListArguments(trackListType = TrackListType.GENRE, trackListId = genreId)
                )
            )
        )
    }

    private fun onTrackSearchResultOverflowMenuIconClicked(trackId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackContextMenu(
                    TrackContextMenuArguments(
                        trackId = trackId,
                        mediaType = MediaType.TRACK,
                        mediaGroup = MediaGroup(
                            mediaId = trackId,
                            mediaGroupType = MediaGroupType.SINGLE_TRACK
                        )
                    )
                )
            )
        )
    }

    private fun onArtistSearchResultOverflowMenuIconClicked(artistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.ArtistContextMenu(
                    ArtistContextMenuArguments(artistId = artistId)
                )
            )
        )
    }

    private fun onAlbumSearchResultOverflowMenuIconClicked(albumId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.AlbumContextMenu(
                    AlbumContextMenuArguments(albumId = albumId)
                )
            )
        )
    }

    private fun onGenreSearchResultOverflowMenuIconClicked(genreId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.GenreContextMenu(
                    GenreContextMenuArguments(genreId = genreId)
                )
            )
        )
    }

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchResultClicked -> {
                when (state.value.selectedOption) {
                    SearchMode.SONGS -> onTrackSearchResultClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultClicked(action.id)
                }
            }
            is SearchUserAction.SearchResultOverflowMenuIconClicked -> {
                when (state.value.selectedOption) {
                    SearchMode.SONGS -> onTrackSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ARTISTS -> onArtistSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.ALBUMS -> onAlbumSearchResultOverflowMenuIconClicked(action.id)
                    SearchMode.GENRES -> onGenreSearchResultOverflowMenuIconClicked(action.id)
                }
            }
            is SearchUserAction.SearchModeChanged -> query.update { it.copy(mode = action.newMode) }
            is SearchUserAction.TextChanged -> query.update { it.copy(term = action.newText) }
            is SearchUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
        }
    }
}

data class SearchState(
    val selectedOption: SearchMode,
    val searchResults: List<ModelListItemState>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            selectedOption = SearchMode.SONGS,
            searchResults = listOf()
        )
    }
}

sealed class SearchUiEvent : UiEvent

sealed interface SearchUserAction : UserAction {
    data class SearchResultClicked(val id: Long) : SearchUserAction
    data class SearchResultOverflowMenuIconClicked(val id: Long) : SearchUserAction
    data class TextChanged(val newText: String) : SearchUserAction
    data class SearchModeChanged(val newMode: SearchMode) : SearchUserAction
    object UpButtonClicked : SearchUserAction
}