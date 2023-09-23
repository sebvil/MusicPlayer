package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.PlaylistTrackCrossRef
import com.sebastianvm.musicplayer.repository.fts.FullTextSearchRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.TrailingButtonType
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.OldBaseViewModel
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
    private val arguments: TrackSearchArguments,
    private val playlistRepository: PlaylistRepository,
    private val ftsRepository: FullTextSearchRepository
) : OldBaseViewModel<TrackSearchState, TrackSearchUserAction>() {

    private val query = MutableStateFlow("")
    private val playlistTrackIds: MutableSet<Long> = mutableSetOf()
    private val queryUpdater =
        combineToPair(query, stateFlow.map { (it as? Data)?.state?.hideTracksInPlaylist == true })
    private val playlistSize = MutableStateFlow(0L)

    init {
        queryUpdater.debounce(SEARCH_DEBOUNCE_TIME_MS)
            .flatMapLatest { (newQuery, hideTracksInPlaylist) ->
                ftsRepository.searchTracks(newQuery).map { tracks ->
                    tracks.map {
                        it.toModelListItemState(
                            if (it.id in playlistTrackIds) TrailingButtonType.Check else TrailingButtonType.Plus
                        )
                    }
                        .filter {
                            !hideTracksInPlaylist || (it.id !in playlistTrackIds)
                        }
                }
            }.onEach { results ->
                setDataState {
                    it.copy(
                        trackSearchResults = results
                    )
                }
            }.launchIn(viewModelScope)

        combineToPair(
            playlistRepository.getPlaylistSize(
                arguments.playlistId
            ),
            playlistRepository.getTrackIdsInPlaylist(arguments.playlistId)
        ).onEach { (size, trackIds) ->

            playlistTrackIds.clear()
            playlistTrackIds.addAll(trackIds)
            playlistSize.update { size }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: TrackSearchUserAction) {
        when (action) {
            is TrackSearchUserAction.CancelAddTrackToPlaylist -> {
                setDataState {
                    it.copy(
                        addTrackConfirmationDialogState = null
                    )
                }
            }

            is TrackSearchUserAction.ConfirmAddTrackToPlaylist -> {
                setDataState {
                    it.copy(
                        addTrackConfirmationDialogState = null
                    )
                }
                addTrackToPlaylist(trackId = action.trackId)
            }

            is TrackSearchUserAction.HideTracksCheckToggled -> {
                setDataState { it.copy(hideTracksInPlaylist = !it.hideTracksInPlaylist) }
            }

            is TrackSearchUserAction.TextChanged -> {
                query.update { action.newText }
            }

            is TrackSearchUserAction.TrackClicked -> {
                if (action.trackId in playlistTrackIds) {
                    setDataState {
                        it.copy(
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
            is TrackSearchUserAction.ToastShown -> setDataState { it.copy(showToast = false) }
        }
    }

    private fun addTrackToPlaylist(trackId: Long) {
        // We do this so the behavior is still the same in case the user presses on tracks very fast
        // and the db is not updated fast enough
        playlistSize.update { it + 1 }
        playlistTrackIds.add(trackId)
        viewModelScope.launch {
            playlistRepository.addTrackToPlaylist(
                PlaylistTrackCrossRef(
                    playlistId = arguments.playlistId,
                    trackId = trackId,
                    position = playlistSize.value
                )
            )
            setDataState { it.copy(showToast = true) }
        }
    }

    override val defaultState: TrackSearchState by lazy {
        TrackSearchState(trackSearchResults = listOf())
    }

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 500L
    }
}

data class TrackSearchArguments(val playlistId: Long)

data class TrackSearchState(
    val trackSearchResults: List<ModelListItemState>,
    val addTrackConfirmationDialogState: AddTrackConfirmationDialogState? = null,
    val hideTracksInPlaylist: Boolean = true,
    val showToast: Boolean = false
) : State

@InstallIn(ViewModelComponent::class)
@Module
object TrackSearchArgumentsModule {
    @Provides
    @ViewModelScoped
    fun trackSearchArgumentsProvider(savedStateHandle: SavedStateHandle): TrackSearchArguments {
        return savedStateHandle.navArgs()
    }
}

sealed interface TrackSearchUserAction : UserAction {
    data class TextChanged(val newText: String) : TrackSearchUserAction
    data class TrackClicked(val trackId: Long, val trackName: String) : TrackSearchUserAction
    data class ConfirmAddTrackToPlaylist(val trackId: Long, val trackName: String) :
        TrackSearchUserAction

    data object CancelAddTrackToPlaylist : TrackSearchUserAction
    data object HideTracksCheckToggled : TrackSearchUserAction
    data object UpButtonClicked : TrackSearchUserAction
    data object ToastShown : TrackSearchUserAction
}

data class AddTrackConfirmationDialogState(val trackId: Long, val trackName: String)
