package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistListViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : BaseViewModel<PlaylistListState, PlaylistListUserAction>() {

    init {
        playlistRepository.getPlaylists().onEach { playlists ->
            if (playlists.isEmpty()) {
                setState { Empty }
            } else {
                setDataState {
                    it.copy(
                        modelListState = ModelListState(
                            items = playlists.map { playlist -> playlist.toModelListItemState() },
                            sortButtonState = null,
                            headerState = HeaderState.None
                        )
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    sortPreferencesRepository.togglePlaylistListSortOder()
                }
            }

            is PlaylistListUserAction.CreatePlaylistButtonClicked -> {
                playlistRepository.createPlaylist(action.playlistName).onEach { playlistId ->
                    if (playlistId == null) {
                        setDataState {
                            it.copy(
                                isPlaylistCreationErrorDialogOpen = true,
                                isCreatePlaylistDialogOpen = false
                            )
                        }
                    } else {
                        setDataState {
                            it.copy(isCreatePlaylistDialogOpen = false)
                        }
                    }
                }.launchIn(viewModelScope)
            }

            is PlaylistListUserAction.DismissPlaylistCreationErrorDialog -> {
                setDataState {
                    it.copy(isPlaylistCreationErrorDialogOpen = false)
                }
            }
        }
    }

    override val defaultState: PlaylistListState by lazy {
        PlaylistListState(
            modelListState = ModelListState(
                items = listOf(),
                sortButtonState = null,
                headerState = HeaderState.None
            ),
            isCreatePlaylistDialogOpen = false,
            isPlaylistCreationErrorDialogOpen = false
        )
    }
}

data class PlaylistListState(
    val modelListState: ModelListState,
    val isCreatePlaylistDialogOpen: Boolean,
    val isPlaylistCreationErrorDialogOpen: Boolean
) : State

sealed interface PlaylistListUserAction : UserAction {
    data object SortByClicked : PlaylistListUserAction
    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction
    data object DismissPlaylistCreationErrorDialog : PlaylistListUserAction
}
