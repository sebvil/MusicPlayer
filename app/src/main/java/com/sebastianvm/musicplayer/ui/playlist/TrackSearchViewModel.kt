package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TrackSearchViewModel @Inject constructor(
    initialState: TrackSearchState,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository,
) :
    BaseViewModel<TrackSearchUiEvent, TrackSearchState>(
        initialState
    ) {

    private val query = MutableStateFlow("")
    private val queryUpdater = combineToPair(query, state.map { it.hideTracksInPlaylist })
    private val playlistSize = MutableStateFlow(0L)

    init {
        queryUpdater.debounce(500).flatMapLatest { (newQuery, hideTracksInPlaylist) ->
            ftsRepository.searchTracks(newQuery).map { tracks ->
                tracks.map { it.toModelListItemState() }.filter {
                    !hideTracksInPlaylist || (it.id !in state.value.playlistTrackIds)
                }
            }
        }.onEach {
            setState {
                copy(
                    trackSearchResults = it
                )
            }
        }.launchIn(viewModelScope)

        combineToPair(
            playlistRepository.getPlaylistSize(state.value.playlistId),
            playlistRepository.getTrackIdsInPlaylist(state.value.playlistId)
        ).onEach { (size, trackIds) ->
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
        if (trackId in state.value.playlistTrackIds) {
            setState {
                copy(
                    addTrackConfirmationDialogState = AddTrackConfirmationDialogState(
                        trackId = trackId,
                        trackName = trackName
                    )
                )
            }
            return
        }
        addTrackToPlaylist(trackId = trackId, trackName = trackName)
    }

    fun onConfirmAddTrackToPlaylist(trackId: Long, trackName: String) {
        setState {
            copy(
                addTrackConfirmationDialogState = null
            )
        }
        addTrackToPlaylist(trackId = trackId, trackName = trackName)
    }

    fun onCancelAddTrackToPlaylist() {
        setState {
            copy(
                addTrackConfirmationDialogState = null
            )
        }
    }

    fun onHideTracksCheckedToggle() {
        setState { copy(hideTracksInPlaylist = !hideTracksInPlaylist) }
    }

    private fun addTrackToPlaylist(trackId: Long, trackName: String) {
        // We do this so the behavior is still the same in case the user presses on tracks very fast
        // and the db is not updated fast enough
        playlistSize.update { it + 1 }
        setState {
            copy(
                playlistTrackIds = playlistTrackIds.toMutableSet().plus(trackId)
            )
        }
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
    val trackSearchResults: List<ModelListItemState>,
    val playlistTrackIds: Set<Long> = setOf(),
    val addTrackConfirmationDialogState: AddTrackConfirmationDialogState? = null,
    val hideTracksInPlaylist: Boolean = true
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
            trackSearchResults = listOf(),
        )
    }
}

sealed class TrackSearchUiEvent : UiEvent {
    data class ShowConfirmationToast(val trackName: String) : TrackSearchUiEvent()
}

data class AddTrackConfirmationDialogState(val trackId: Long, val trackName: String)