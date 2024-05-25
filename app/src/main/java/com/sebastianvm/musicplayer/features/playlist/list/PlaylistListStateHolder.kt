package com.sebastianvm.musicplayer.features.playlist.list

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.features.navigation.Screen
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuStateHolder
import com.sebastianvm.musicplayer.features.playlist.menu.playlistContextMenuStateHolderFactory
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
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolder
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

data class PlaylistListState(
    val modelListState: ModelListState,
    val isCreatePlaylistDialogOpen: Boolean,
    val isPlaylistCreationErrorDialogOpen: Boolean,
    val playlistContextMenuStateHolder: PlaylistContextMenuStateHolder? = null
) : State

sealed interface PlaylistListUserAction : UserAction {
    data object SortByClicked : PlaylistListUserAction
    data class PlaylistMoreIconClicked(val playlistId: Long) : PlaylistListUserAction
    data object PlaylistContextMenuDismissed : PlaylistListUserAction
    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction
    data object DismissPlaylistCreationErrorDialog : PlaylistListUserAction
}

class PlaylistListStateHolder(
    private val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playlistRepository: PlaylistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val playlistContextMenuStateHolderFactory:
    StateHolderFactory<PlaylistContextMenuArguments, PlaylistContextMenuDelegate, PlaylistContextMenuStateHolder>,
) : StateHolder<UiState<PlaylistListState>, PlaylistListUserAction> {

    private val isPlayListCreationErrorDialogOpen = MutableStateFlow(false)
    private val isCreatePlaylistDialogOpen = MutableStateFlow(false)
    private val _contextMenuPlaylistId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<UiState<PlaylistListState>> =
        combine(
            playlistRepository.getPlaylists(),
            isPlayListCreationErrorDialogOpen,
            isCreatePlaylistDialogOpen,
            _contextMenuPlaylistId
        ) { playlists, isPlaylistCreationErrorDialogOpen, isCreatePlaylistDialogOpen, contextMenuPlaylistId ->
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
                        isPlaylistCreationErrorDialogOpen = isPlaylistCreationErrorDialogOpen,
                        playlistContextMenuStateHolder = contextMenuPlaylistId?.let { playlistId ->
                            playlistContextMenuStateHolderFactory.getStateHolder(
                                arguments = PlaylistContextMenuArguments(playlistId),
                                delegate = object : PlaylistContextMenuDelegate {
                                    override fun push(screen: Screen<*>) {
                                        _contextMenuPlaylistId.update { null }
                                        push(screen)
                                    }

                                    override fun pop() {
                                        _contextMenuPlaylistId.update { null }
                                    }
                                }
                            )
                        }
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

            is PlaylistListUserAction.PlaylistMoreIconClicked -> {
                _contextMenuPlaylistId.update { action.playlistId }
            }

            is PlaylistListUserAction.PlaylistContextMenuDismissed -> {
                _contextMenuPlaylistId.update { null }
            }
        }
    }
}

@Composable
fun rememberPlaylistListStateHolder(): PlaylistListStateHolder {
    val playlistContextMenuStateHolderFactory = playlistContextMenuStateHolderFactory()
    return stateHolder { dependencyContainer ->
        PlaylistListStateHolder(
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
            playlistContextMenuStateHolderFactory = playlistContextMenuStateHolderFactory,
        )
    }
}
