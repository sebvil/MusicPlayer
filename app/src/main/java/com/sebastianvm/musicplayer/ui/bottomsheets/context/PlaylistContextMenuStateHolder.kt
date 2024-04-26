package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistContextMenuStateHolder(
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: PlaylistContextMenuArguments,
    private val playlistRepository: PlaylistRepository,
) : BaseContextMenuStateHolder() {

    private val playlistId = arguments.playlistId

    private val showDeleteConfirmationDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<UiState<ContextMenuState>> =
        combine(
            playlistRepository.getPlaylistName(playlistId),
            showDeleteConfirmationDialog
        ) { playlistName, showDeleteConfirmationDialog ->
            Data(
                ContextMenuState(
                    menuTitle = playlistName,
                    listItems = listOf(
                        ContextMenuItem.PlayAllSongs,
                        ContextMenuItem.ViewPlaylist(playlistId = playlistId),
                        ContextMenuItem.DeletePlaylist
                    ),
                    showDeleteConfirmationDialog = showDeleteConfirmationDialog
                )
            )
        }.stateIn(stateHolderScope, SharingStarted.Eagerly, Loading)

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.DeletePlaylist -> {
                showDeleteConfirmationDialog.update { true }
            }

            else -> error("Invalid row for playlist context menu")
        }
    }

    override fun onConfirmDeleteClicked() {
        stateHolderScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            showDeleteConfirmationDialog.update { false }
        }
    }

    override fun onCancelDeleteClicked() {
        showDeleteConfirmationDialog.update { false }
    }
}

data class PlaylistContextMenuArguments(val playlistId: Long)
