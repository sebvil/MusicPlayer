package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.repository.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrackSearchViewModel @Inject constructor(
    initialState: TrackSearchState,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository,
) :
    BaseViewModel<TrackSearchUiEvent, TrackSearchState>(initialState) {

    private val query = MutableStateFlow("")
    private val playlistSize = MutableStateFlow(0L)

    init {
        setState {
            copy(
                trackSearchResults = query.flatMapLatest {
                    Pager(PagingConfig(pageSize = 20)) {
                        ftsRepository.searchTracksPaged(it)
                    }.flow.mapLatest { pagingData ->
                        pagingData.map { it.track.toTrackRowState(includeTrackNumber = false) }
                    }
                },
            )
        }
        combine(
            playlistRepository.getPlaylistSize(state.value.playlistId),
            playlistRepository.getTrackIdsInPlaylist(state.value.playlistId)
        ) { size, trackIds ->
            Pair(size, trackIds)
        }.onEach { (size, trackIds) ->
            setState {
                copy(
                    playlistTrackIds = trackIds
                )
            }
            playlistSize.update { size }
        }.launchIn(viewModelScope)

    }

    fun onTextChanged(newText: String) {
        query.update { newText }
    }

    fun onTrackClicked(trackId: Long, trackName: String) {
        playlistSize.update { it + 1 }
        viewModelScope.launch {
            playlistRepository.addTrackToPlaylist(
                PlaylistTrackCrossRef(
                    playlistId = state.value.playlistId,
                    trackId = trackId,
                    position = playlistSize.value
                )
            )
            addUiEvent(TrackSearchUiEvent.ShowConfirmationToast(trackName))
        }
    }
}

data class TrackSearchState(
    val playlistId: Long,
    val trackSearchResults: Flow<PagingData<TrackRowState>>,
    val playlistTrackIds: Set<Long> = setOf()
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackSearchStateProvider(savedStateHandle: SavedStateHandle): TrackSearchState {
        val args = savedStateHandle.getArgs<TrackSearchArguments>()
        return TrackSearchState(
            playlistId = args.playlistId,
            trackSearchResults = emptyFlow(),
        )
    }
}

sealed class TrackSearchUiEvent : UiEvent {
    data class ShowConfirmationToast(val trackName: String) : TrackSearchUiEvent()
}