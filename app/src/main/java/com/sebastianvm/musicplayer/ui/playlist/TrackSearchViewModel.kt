package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.util.coroutines.combineToPair
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
) : BaseViewModel<TrackSearchState, TrackSearchUserAction>(initialState) {

    private val query = MutableStateFlow("")
    private val queryUpdater = combineToPair(query, stateFlow.map { it.hideTracksInPlaylist })
    private val playlistSize = MutableStateFlow(0L)

    init {
        queryUpdater.debounce(500).flatMapLatest { (newQuery, hideTracksInPlaylist) ->
            ftsRepository.searchTracks(newQuery).map { tracks ->
                tracks.map { it.toModelListItemState() }.filter {
                    !hideTracksInPlaylist || (it.id !in state.playlistTrackIds)
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
            playlistRepository.getPlaylistSize(
                state.playlistId
            ),
            playlistRepository.getTrackIdsInPlaylist(state.playlistId)
        ).onEach { (size, trackIds) ->
            setState {
                copy(
                    playlistTrackIds = trackIds
                )
            }
            playlistSize.update { size }
        }.launchIn(viewModelScope)

    }

    override fun handle(action: TrackSearchUserAction) {
        when (action) {
            is TrackSearchUserAction.CancelAddTrackToPlaylist -> {
                setState {
                    copy(
                        addTrackConfirmationDialogState = null
                    )
                }
            }

            is TrackSearchUserAction.ConfirmAddTrackToPlaylist -> {
                setState {
                    copy(
                        addTrackConfirmationDialogState = null
                    )
                }
                addTrackToPlaylist(trackId = action.trackId)
            }

            is TrackSearchUserAction.HideTracksCheckToggled -> {
                setState { copy(hideTracksInPlaylist = !hideTracksInPlaylist) }
            }

            is TrackSearchUserAction.TextChanged -> {
                query.update { action.newText }
            }

            is TrackSearchUserAction.TrackClicked -> {
                if (action.trackId in state.playlistTrackIds) {
                    setState {
                        copy(
                            addTrackConfirmationDialogState = AddTrackConfirmationDialogState(
                                trackId = action.trackId,
                                trackName = action.trackName
                            )
                        )
                    }
                    return
                }
                addTrackToPlaylist(trackId = action.trackId)
            }

            is TrackSearchUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is TrackSearchUserAction.ToastShown -> setState { copy(showToast = false) }
        }
    }


    private fun addTrackToPlaylist(trackId: Long) {
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
                    playlistId = state.playlistId,
                    trackId = trackId,
                    position = playlistSize.value
                )
            )
            setState { copy(showToast = true) }
        }
    }
}

data class TrackSearchState(
    val playlistId: Long,
    val trackSearchResults: List<ModelListItemState>,
    val playlistTrackIds: Set<Long> = setOf(),
    val addTrackConfirmationDialogState: AddTrackConfirmationDialogState? = null,
    val hideTracksInPlaylist: Boolean = true,
    val showToast: Boolean = false
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackSearchStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackSearchStateProvider(savedStateHandle: SavedStateHandle): TrackSearchState {
        val args = savedStateHandle.navArgs<TrackSearchArguments>()
        return TrackSearchState(
            playlistId = args.playlistId,
            trackSearchResults = listOf(),
        )
    }
}

sealed interface TrackSearchUserAction : UserAction {
    data class TextChanged(val newText: String) : TrackSearchUserAction
    data class TrackClicked(val trackId: Long, val trackName: String) : TrackSearchUserAction
    data class ConfirmAddTrackToPlaylist(val trackId: Long, val trackName: String) :
        TrackSearchUserAction

    object CancelAddTrackToPlaylist : TrackSearchUserAction
    object HideTracksCheckToggled : TrackSearchUserAction
    object UpButtonClicked : TrackSearchUserAction
    object ToastShown : TrackSearchUserAction
}

data class AddTrackConfirmationDialogState(val trackId: Long, val trackName: String)