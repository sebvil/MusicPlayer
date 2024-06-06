package com.sebastianvm.musicplayer.ui.playlist

import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackSearchStateHolder(
    private val arguments: TrackSearchArguments,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<UiState<TrackSearchState>, TrackSearchUserAction> {

    private val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchResults =
        query.debounce(SEARCH_DEBOUNCE_TIME_MS).flatMapLatest { ftsRepository.searchTracks(it) }

    private val hideTracksInPlaylist = MutableStateFlow(true)
    private val addTrackConfirmationDialogState =
        MutableStateFlow<AddTrackConfirmationDialogState?>(null)
    private val showToast = MutableStateFlow(false)

    override val state: StateFlow<UiState<TrackSearchState>> =
        combine(
                searchResults,
                hideTracksInPlaylist,
                playlistRepository.getTrackIdsInPlaylist(arguments.playlistId),
                addTrackConfirmationDialogState,
                showToast,
            ) {
                tracks,
                hideTracksInPlaylist,
                playlistTrackIds,
                addTrackConfirmationDialogState,
                showToast ->
                val results =
                    tracks
                        .filter { !hideTracksInPlaylist || (it.id !in playlistTrackIds) }
                        .map {
                            it.toModelListItemState(
                                trailingButtonType =
                                    if (it.id in playlistTrackIds) TrailingButtonType.Check
                                    else TrailingButtonType.Plus
                            )
                        }
                if (results.isEmpty()) {
                    Empty
                } else {
                    Data(
                        TrackSearchState(
                            trackSearchResults = results,
                            hideTracksInPlaylist = hideTracksInPlaylist,
                            addTrackConfirmationDialogState = addTrackConfirmationDialogState,
                            showToast = showToast,
                        )
                    )
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: TrackSearchUserAction) {
        when (action) {
            is TrackSearchUserAction.CancelAddTrackToPlaylist -> {
                addTrackConfirmationDialogState.update { null }
            }
            is TrackSearchUserAction.ConfirmAddTrackToPlaylist -> {
                addTrackConfirmationDialogState.update { null }
                addTrackToPlaylist(trackId = action.trackId)
            }
            is TrackSearchUserAction.HideTracksCheckToggled -> {
                hideTracksInPlaylist.update { !it }
            }
            is TrackSearchUserAction.TextChanged -> {
                query.update { action.newText }
            }
            is TrackSearchUserAction.TrackClicked -> {
                stateHolderScope.launch {
                    val playlistTrackIds =
                        playlistRepository.getTrackIdsInPlaylist(arguments.playlistId).first()
                    if (action.trackId in playlistTrackIds) {
                        addTrackConfirmationDialogState.update {
                            AddTrackConfirmationDialogState(
                                trackId = action.trackId,
                                trackName = action.trackName,
                            )
                        }
                        return@launch
                    }
                    addTrackToPlaylist(trackId = action.trackId)
                }
            }
            is TrackSearchUserAction.ToastShown -> showToast.update { false }
        }
    }

    private fun addTrackToPlaylist(trackId: Long) {
        // TODO use mutex to prevent race condition
        stateHolderScope.launch {
            val playlistSize = playlistRepository.getPlaylistSize(arguments.playlistId).first()
            playlistRepository.addTrackToPlaylist(
                PlaylistTrackCrossRef(
                    playlistId = arguments.playlistId,
                    trackId = trackId,
                    position = playlistSize,
                )
            )
            showToast.update { true }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 500L
    }
}

data class TrackSearchArguments(val playlistId: Long) : Arguments

data class TrackSearchState(
    val trackSearchResults: List<ModelListItemState>,
    val addTrackConfirmationDialogState: AddTrackConfirmationDialogState? = null,
    val hideTracksInPlaylist: Boolean = true,
    val showToast: Boolean = false,
) : State

sealed interface TrackSearchUserAction : UserAction {
    data class TextChanged(val newText: String) : TrackSearchUserAction

    data class TrackClicked(val trackId: Long, val trackName: String) : TrackSearchUserAction

    data class ConfirmAddTrackToPlaylist(val trackId: Long, val trackName: String) :
        TrackSearchUserAction

    data object CancelAddTrackToPlaylist : TrackSearchUserAction

    data object HideTracksCheckToggled : TrackSearchUserAction

    data object ToastShown : TrackSearchUserAction
}

data class AddTrackConfirmationDialogState(val trackId: Long, val trackName: String)
