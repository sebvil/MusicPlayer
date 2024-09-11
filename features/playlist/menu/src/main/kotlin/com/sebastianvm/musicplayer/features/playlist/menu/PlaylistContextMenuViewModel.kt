package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuProps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PlaylistContextMenuState : State {
    data class Data(
        val playlistName: String,
        val playlistId: Long,
        val showDeleteConfirmationDialog: Boolean,
    ) : PlaylistContextMenuState

    data object Loading : PlaylistContextMenuState
}

sealed interface PlaylistContextMenuUserAction : UserAction {
    data object PlayPlaylistClicked : PlaylistContextMenuUserAction

    data object DeletePlaylistClicked : PlaylistContextMenuUserAction

    data object ConfirmPlaylistDeletionClicked : PlaylistContextMenuUserAction

    data object PlaylistDeletionCancelled : PlaylistContextMenuUserAction
}

class PlaylistContextMenuViewModel(
    arguments: PlaylistContextMenuArguments,
    playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
    vmScope: CoroutineScope = getViewModelScope(),
    private val props: StateFlow<PlaylistContextMenuProps>,
) :
    BaseViewModel<PlaylistContextMenuState, PlaylistContextMenuUserAction>(
        viewModelScope = vmScope
    ) {

    private val playlistId = arguments.playlistId
    private val showDeleteConfirmationDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<PlaylistContextMenuState> =
        combine(playlistRepository.getPlaylistName(playlistId), showDeleteConfirmationDialog) {
                playlistName,
                showDeleteConfirmationDialog ->
                PlaylistContextMenuState.Data(
                    playlistName = playlistName,
                    playlistId = playlistId,
                    showDeleteConfirmationDialog = showDeleteConfirmationDialog,
                )
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, PlaylistContextMenuState.Loading)

    override fun handle(action: PlaylistContextMenuUserAction) {
        when (action) {
            is PlaylistContextMenuUserAction.PlayPlaylistClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Playlist(playlistId))
                }
            }
            is PlaylistContextMenuUserAction.DeletePlaylistClicked -> {
                showDeleteConfirmationDialog.update { true }
            }
            is PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked -> {
                props.value.deletePlaylist()
            }
            is PlaylistContextMenuUserAction.PlaylistDeletionCancelled -> {
                showDeleteConfirmationDialog.update { false }
            }
        }
    }
}
