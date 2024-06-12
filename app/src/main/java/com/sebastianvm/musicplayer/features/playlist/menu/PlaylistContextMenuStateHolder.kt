package com.sebastianvm.musicplayer.features.playlist.menu

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.extensions.collectValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaylistContextMenuArguments(val playlistId: Long) : Arguments

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

interface PlaylistContextMenuDelegate {
    fun deletePlaylist()
}

class PlaylistContextMenuStateHolder(
    arguments: PlaylistContextMenuArguments,
    playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val delegate: PlaylistContextMenuDelegate,
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<PlaylistContextMenuState, PlaylistContextMenuUserAction> {

    private val playlistId = arguments.playlistId
    private val showDeleteConfirmationDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<PlaylistContextMenuState> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val playlistName =
                playlistRepository.getPlaylistName(playlistId).collectValue(initial = null)
            val showDeleteConfirmationDialog = showDeleteConfirmationDialog.collectValue()
            if (playlistName == null) {
                PlaylistContextMenuState.Loading
            } else {
                PlaylistContextMenuState.Data(
                    playlistName = playlistName,
                    playlistId = playlistId,
                    showDeleteConfirmationDialog = showDeleteConfirmationDialog,
                )
            }
        }

    override fun handle(action: PlaylistContextMenuUserAction) {
        when (action) {
            is PlaylistContextMenuUserAction.PlayPlaylistClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Playlist(playlistId))
                }
            }
            is PlaylistContextMenuUserAction.DeletePlaylistClicked -> {
                showDeleteConfirmationDialog.update { true }
            }
            is PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked -> {
                delegate.deletePlaylist()
            }
            is PlaylistContextMenuUserAction.PlaylistDeletionCancelled -> {
                showDeleteConfirmationDialog.update { false }
            }
        }
    }
}
