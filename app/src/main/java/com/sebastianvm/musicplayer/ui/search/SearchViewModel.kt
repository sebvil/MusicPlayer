package com.sebastianvm.musicplayer.ui.search

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    initialState: SearchState,
    private val ftsRepository: FullTextSearchRepository,
    private val playbackManager: PlaybackManager,
) :
    BaseViewModel<SearchUiEvent, SearchState>(initialState) {

    init {
        val searchTerm = state.map { it.searchTerm }
        setState {
            copy(
                trackSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchTracksPaged(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.track.toTrackRowState(includeTrackNumber = false) }
                    }
                },
                artistSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchArtists(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.toArtistRowState(shouldShowContextMenu = true) }
                    }
                },
                albumSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchAlbums(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.album.toAlbumRowState() }
                    }
                },
                genreSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchGenres(it)
                    }.flow
                },
            )

        }
    }

    fun <A : UserAction> handle(action: A) {
        when (action) {
            is SearchUserAction.OnTextChanged -> {
                setState {
                    copy(
                        searchTerm = action.newText,
                    )
                }
            }
            is SearchUserAction.SearchTypeChanged -> {
                setState {
                    copy(
                        selectedOption = action.newType
                    )
                }
            }
            is SearchUserAction.TrackRowClicked -> {
                viewModelScope.launch {
                    playbackManager.playSingleTrack(action.trackId)
                    addUiEvent(SearchUiEvent.NavigateToPlayer)
                }
            }
            is SearchUserAction.ArtistRowClicked -> addUiEvent(SearchUiEvent.NavigateToArtist(action.artistId))
            is SearchUserAction.AlbumRowClicked -> addUiEvent(SearchUiEvent.NavigateToAlbum(action.albumId))
            is SearchUserAction.GenreRowClicked -> addUiEvent(SearchUiEvent.NavigateToGenre(action.genreId))
            is SearchUserAction.TrackOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaType = MediaType.TRACK,
                        mediaGroup = MediaGroup(
                            MediaGroupType.SINGLE_TRACK,
                            action.trackId
                        ),
                    )
                )
            }
            is SearchUserAction.ArtistOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaType = MediaType.ARTIST,
                        mediaGroup = MediaGroup(
                            MediaGroupType.ARTIST,
                            action.artistId
                        ),
                    )
                )
            }
            is SearchUserAction.AlbumOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaType = MediaType.ALBUM,
                        mediaGroup = MediaGroup(
                            MediaGroupType.ALBUM,
                            action.albumId
                        ),
                    )
                )
            }
            is SearchUserAction.GenreOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaType = MediaType.GENRE,
                        mediaGroup = MediaGroup(
                            MediaGroupType.GENRE,
                            action.genreId
                        ),
                    )
                )
            }

        }
    }
}

data class SearchState(
    @StringRes val selectedOption: Int,
    val searchTerm: String = "",
    val trackSearchResults: Flow<PagingData<TrackRowState>>,
    val artistSearchResults: Flow<PagingData<ArtistRowState>>,
    val albumSearchResults: Flow<PagingData<AlbumRowState>>,
    val genreSearchResults: Flow<PagingData<Genre>>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            selectedOption = R.string.songs,
            trackSearchResults = flow {},
            artistSearchResults = flow {},
            albumSearchResults = flow {},
            genreSearchResults = flow {},
        )
    }
}

sealed class SearchUserAction : UserAction {
    data class OnTextChanged(val newText: String) : SearchUserAction()
    data class SearchTypeChanged(@StringRes val newType: Int) : SearchUserAction()
    data class TrackRowClicked(val trackId: Long) : SearchUserAction()
    data class TrackOverflowMenuClicked(val trackId: Long) : SearchUserAction()
    data class ArtistRowClicked(val artistId: Long) : SearchUserAction()
    data class ArtistOverflowMenuClicked(val artistId: Long) : SearchUserAction()
    data class AlbumRowClicked(val albumId: Long) : SearchUserAction()
    data class AlbumOverflowMenuClicked(val albumId: Long) : SearchUserAction()
    data class GenreRowClicked(val genreId: Long) : SearchUserAction()
    data class GenreOverflowMenuClicked(val genreId: Long) : SearchUserAction()
}

sealed class SearchUiEvent : UiEvent {
    object NavigateToPlayer : SearchUiEvent()
    data class NavigateToArtist(val artistId: Long) : SearchUiEvent()
    data class NavigateToAlbum(val albumId: Long) : SearchUiEvent()
    data class NavigateToGenre(val genreId: Long) : SearchUiEvent()
    data class OpenContextMenu(val mediaType: MediaType, val mediaGroup: MediaGroup) :
        SearchUiEvent()

}
