package com.sebastianvm.musicplayer.ui.search

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playback.MEDIA_GROUP
import com.sebastianvm.musicplayer.repository.playback.PlaybackServiceRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
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
    private val playbackServiceRepository: PlaybackServiceRepository,
    private val mediaQueueRepository: MediaQueueRepository,
) :
    BaseViewModel<SearchUserAction, SearchUiEvent, SearchState>(initialState) {

    init {
        val searchTerm = state.map { it.searchTerm }
        setState {
            copy(
                trackSearchResults = searchTerm.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchTracks(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.toTrackRowState(includeTrackNumber = false) }
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
                        pagingData.map { it.toAlbumRowState() }
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

    override fun handle(action: SearchUserAction) {
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
                val transportControls = playbackServiceRepository.transportControls
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(
                        mediaType = MediaType.SINGLE_TRACK,
                        mediaId = action.trackId
                    )
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = SortOrder.ASCENDING,
                        sortOption = SortOption.TRACK_NAME
                    )
                    val extras = Bundle().apply {
                        putParcelable(MEDIA_GROUP, mediaGroup)
                    }
                    transportControls.playFromMediaId(action.trackId, extras)
                    addUiEvent(SearchUiEvent.NavigateToPlayer)
                }
            }
            is SearchUserAction.ArtistRowClicked -> addUiEvent(SearchUiEvent.NavigateToArtist(action.artistName))
            is SearchUserAction.AlbumRowClicked -> addUiEvent(SearchUiEvent.NavigateToAlbum(action.albumId))
            is SearchUserAction.GenreRowClicked -> addUiEvent(SearchUiEvent.NavigateToGenre(action.genreName))
            is SearchUserAction.TrackOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaGroup = MediaGroup(
                            MediaType.SINGLE_TRACK,
                            action.trackId
                        ),
                        sortOption = SortOption.TRACK_NAME,
                        sortOrder = SortOrder.ASCENDING
                    )
                )
            }
            is SearchUserAction.ArtistOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaGroup = MediaGroup(
                            MediaType.ARTIST,
                            action.artistName
                        ),
                        sortOption = SortOption.TRACK_NAME,
                        sortOrder = SortOrder.ASCENDING
                    )
                )
            }
            is SearchUserAction.AlbumOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaGroup = MediaGroup(
                            MediaType.ALBUM,
                            action.albumId
                        ),
                        sortOption = SortOption.ALBUM_NAME,
                        sortOrder = SortOrder.ASCENDING
                    )
                )
            }
            is SearchUserAction.GenreOverflowMenuClicked -> {
                addUiEvent(
                    SearchUiEvent.OpenContextMenu(
                        mediaGroup = MediaGroup(
                            MediaType.GENRE,
                            action.genreName
                        ),
                        sortOption = SortOption.TRACK_NAME,
                        sortOrder = SortOrder.ASCENDING
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
    val genreSearchResults: Flow<PagingData<Genre>>,
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
    data class TrackRowClicked(val trackId: String) : SearchUserAction()
    data class TrackOverflowMenuClicked(val trackId: String) : SearchUserAction()
    data class ArtistRowClicked(val artistName: String) : SearchUserAction()
    data class ArtistOverflowMenuClicked(val artistName: String) : SearchUserAction()
    data class AlbumRowClicked(val albumId: String) : SearchUserAction()
    data class AlbumOverflowMenuClicked(val albumId: String) : SearchUserAction()
    data class GenreRowClicked(val genreName: String) : SearchUserAction()
    data class GenreOverflowMenuClicked(val genreName: String) : SearchUserAction()
}

sealed class SearchUiEvent : UiEvent {
    object NavigateToPlayer : SearchUiEvent()
    data class NavigateToArtist(val artistName: String) : SearchUiEvent()
    data class NavigateToAlbum(val albumId: String) : SearchUiEvent()
    data class NavigateToGenre(val genreName: String) : SearchUiEvent()
    data class OpenContextMenu(
        val mediaGroup: MediaGroup,
        val sortOption: SortOption,
        val sortOrder: SortOrder
    ) : SearchUiEvent()

}
