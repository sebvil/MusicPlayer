package com.sebastianvm.musicplayer.ui.library.playlistlist

import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.HeaderState
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Empty
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistListStateHolder(
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playlistRepository: PlaylistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository
) : StateHolder<UiState<PlaylistListState>, PlaylistListUserAction> {

    private val isPlayListCreationErrorDialogOpen = MutableStateFlow(false)
    private val isCreatePlaylistDialogOpen = MutableStateFlow(false)

    override val state: StateFlow<UiState<PlaylistListState>> =
        combine(
            playlistRepository.getPlaylists(),
            isPlayListCreationErrorDialogOpen,
            isCreatePlaylistDialogOpen
        ) { playlists, isPlaylistCreationErrorDialogOpen, isCreatePlaylistDialogOpen ->
            if (playlists.isEmpty()) {
                Empty
            } else {
                Data(
                    PlaylistListState(
                        modelListState = ModelListState(
                            items = playlists.map { playlist -> playlist.toModelListItemState() },
                            sortButtonState = null,
                            headerState = HeaderState.None
                        ),
                        isCreatePlaylistDialogOpen = isCreatePlaylistDialogOpen,
                        isPlaylistCreationErrorDialogOpen = isPlaylistCreationErrorDialogOpen
                    )
                )
            }
        }.stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.SortByClicked -> {
                stateHolderScope.launch {
                    sortPreferencesRepository.togglePlaylistListSortOder()
                }
            }

            is PlaylistListUserAction.CreatePlaylistButtonClicked -> {
                playlistRepository.createPlaylist(action.playlistName)
                    .onEach { playlistId ->
                        if (playlistId == null) {
                            isPlayListCreationErrorDialogOpen.update { true }
                            isCreatePlaylistDialogOpen.update { false }
                        } else {
                            isCreatePlaylistDialogOpen.update { false }
                        }
                    }.launchIn(stateHolderScope)
            }

            is PlaylistListUserAction.DismissPlaylistCreationErrorDialog -> {
                isPlayListCreationErrorDialogOpen.update { false }
            }
        }
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