package com.sebastianvm.musicplayer.ui.search

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.database.entities.Genre
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    initialState: SearchState,
    private val ftsRepository: FullTextSearchRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val mediaQueueRepository: MediaQueueRepository,
) :
    BaseViewModel<SearchUserAction, SearchUiEvent, SearchState>(initialState) {

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.OnTextChanged -> {
                collectFirst(ftsRepository.searchTracks(action.newText)) { tracks ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            trackSearchResults = tracks.map { it.toTrackRowState() },
                        )
                    }
                }

                collectFirst(ftsRepository.searchArtists(action.newText)) { artists ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            artistSearchResults = artists.map {
                                it.toArtistRowState(
                                    shouldShowContextMenu = true
                                )
                            }
                        )
                    }
                }

                collectFirst(ftsRepository.searchAlbums(action.newText)) { albums ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            albumSearchResults = albums.map { it.toAlbumRowState() }
                        )
                    }
                }

                collectFirst(ftsRepository.searchGenres(action.newText)) { genres ->
                    setState {
                        copy(
                            searchTerm = action.newText,
                            genreSearchResults = genres
                        )
                    }
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
                val transportControls = musicServiceConnection.transportControls
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
            is SearchUserAction.ArtistRowClicked -> TODO()
            is SearchUserAction.AlbumRowClicked -> TODO()
            is SearchUserAction.GenreRowClicked -> TODO()
            is SearchUserAction.TrackOverflowMenuClicked -> TODO()
            is SearchUserAction.ArtistOverflowMenuClicked -> TODO()
            is SearchUserAction.AlbumOverflowMenuClicked -> TODO()
            is SearchUserAction.GenreOverflowMenuClicked -> TODO()

        }
    }
}

data class SearchState(
    val searchTerm: String,
    @StringRes val selectedOption: Int,
    val trackSearchResults: List<TrackRowState>,
    val artistSearchResults: List<ArtistRowState>,
    val albumSearchResults: List<AlbumRowState>,
    val genreSearchResults: List<Genre>,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialSearchStateProvider(): SearchState {
        return SearchState(
            searchTerm = "",
            selectedOption = R.string.songs,
            trackSearchResults = listOf(),
            artistSearchResults = listOf(),
            albumSearchResults = listOf(),
            genreSearchResults = listOf(),
        )
    }
}

sealed class SearchUserAction : UserAction {
    data class OnTextChanged(val newText: String) : SearchUserAction()
    data class SearchTypeChanged(@StringRes val newType: Int) : SearchUserAction()
    data class TrackRowClicked(val trackId: String) : SearchUserAction()
    data class TrackOverflowMenuClicked(val trackId: String) : SearchUserAction()
    data class ArtistRowClicked(val artistId: String) : SearchUserAction()
    data class ArtistOverflowMenuClicked(val artistId: String) : SearchUserAction()
    data class AlbumRowClicked(val albumId: String) : SearchUserAction()
    data class AlbumOverflowMenuClicked(val albumId: String) : SearchUserAction()
    data class GenreRowClicked(val genreName: String) : SearchUserAction()
    data class GenreOverflowMenuClicked(val genreName: String) : SearchUserAction()
}

sealed class SearchUiEvent : UiEvent {
    object NavigateToPlayer : SearchUiEvent()
}

