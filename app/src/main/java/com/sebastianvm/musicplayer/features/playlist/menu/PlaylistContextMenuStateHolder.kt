package com.sebastianvm.musicplayer.features.playlist.menu

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    data object ViewPlaylistClicked : PlaylistContextMenuUserAction
    data object DeletePlaylistClicked : PlaylistContextMenuUserAction
    data object ConfirmPlaylistDeletionClicked : PlaylistContextMenuUserAction
    data object PlaylistDeletionCancelled : PlaylistContextMenuUserAction
}

class PlaylistContextMenuStateHolder(
    arguments: PlaylistContextMenuArguments,
    private val playlistRepository: PlaylistRepository,
    private val navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<PlaylistContextMenuState, PlaylistContextMenuUserAction> {

    private val playlistId = arguments.playlistId
    private val showDeleteConfirmationDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<PlaylistContextMenuState> =

        combine(
            playlistRepository.getPlaylistName(playlistId),
            showDeleteConfirmationDialog
        ) { playlistName, showDeleteConfirmationDialog ->
            PlaylistContextMenuState.Data(
                playlistName = playlistName,
                playlistId = playlistId,
                showDeleteConfirmationDialog = showDeleteConfirmationDialog,
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, PlaylistContextMenuState.Loading)

    override fun handle(action: PlaylistContextMenuUserAction) {
        when (action) {
            is PlaylistContextMenuUserAction.PlayPlaylistClicked -> {
            }

            is PlaylistContextMenuUserAction.ViewPlaylistClicked -> {
                navController.push(
                    TrackList(
                        arguments = TrackListArguments(
                            MediaGroup.Playlist(
                                playlistId
                            )
                        ),
                        navController = navController
                    ),
                    navOptions = NavOptions(popCurrent = true)
                )
            }

            is PlaylistContextMenuUserAction.DeletePlaylistClicked -> {
                showDeleteConfirmationDialog.update { true }
            }

            is PlaylistContextMenuUserAction.ConfirmPlaylistDeletionClicked -> {
                stateHolderScope.launch {
                    playlistRepository.deletePlaylist(playlistId)
                    showDeleteConfirmationDialog.update { false }
                }
            }

            is PlaylistContextMenuUserAction.PlaylistDeletionCancelled -> {
                showDeleteConfirmationDialog.update { false }
            }
        }
    }
}

fun getPlaylistContextMenuStateHolder(
    dependencies: DependencyContainer,
    arguments: PlaylistContextMenuArguments,
    navController: NavController
): PlaylistContextMenuStateHolder {
    return PlaylistContextMenuStateHolder(
        arguments = arguments,
        playlistRepository = dependencies.repositoryProvider.playlistRepository,
        navController = navController
    )
}
