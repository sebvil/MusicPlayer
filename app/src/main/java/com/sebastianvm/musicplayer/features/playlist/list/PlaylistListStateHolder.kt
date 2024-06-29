package com.sebastianvm.musicplayer.features.playlist.list

import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.designsystem.components.PlaylistRow
import com.sebastianvm.musicplayer.designsystem.components.SortButton
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.navigation.NavOptions
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.playlist.details.PlaylistDetailsUiComponent
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenu
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
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

sealed interface PlaylistListState : State {

    val isCreatePlaylistDialogOpen: Boolean
    val isPlaylistCreationErrorDialogOpen: Boolean

    data class Data(
        val playlists: List<PlaylistRow.State>,
        val sortButtonState: SortButton.State,
        override val isCreatePlaylistDialogOpen: Boolean,
        override val isPlaylistCreationErrorDialogOpen: Boolean,
    ) : PlaylistListState

    data class Empty(
        override val isCreatePlaylistDialogOpen: Boolean,
        override val isPlaylistCreationErrorDialogOpen: Boolean,
    ) : PlaylistListState

    data object Loading : PlaylistListState {
        override val isCreatePlaylistDialogOpen: Boolean = false
        override val isPlaylistCreationErrorDialogOpen: Boolean = false
    }
}

sealed interface PlaylistListUserAction : UserAction {
    data object SortByClicked : PlaylistListUserAction

    data class PlaylistMoreIconClicked(val playlistId: Long) : PlaylistListUserAction

    data class PlaylistClicked(val playlistId: Long, val playlistName: String) :
        PlaylistListUserAction

    data object CreateNewPlaylistButtonClicked : PlaylistListUserAction

    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction

    data object DismissPlaylistCreationErrorDialog : PlaylistListUserAction

    data object DismissPlaylistCreationDialog : PlaylistListUserAction
}

class PlaylistListStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playlistRepository: PlaylistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val navController: NavController,
) : StateHolder<PlaylistListState, PlaylistListUserAction> {

    private val isPlayListCreationErrorDialogOpen = MutableStateFlow(false)
    private val isCreatePlaylistDialogOpen = MutableStateFlow(false)
    private val sortOrder = sortPreferencesRepository.getPlaylistsListSortOrder()

    override val state: StateFlow<PlaylistListState> =
        combine(
                playlistRepository.getPlaylists(),
                isPlayListCreationErrorDialogOpen,
                isCreatePlaylistDialogOpen,
                sortOrder,
            ) { playlists, isPlaylistCreationErrorDialogOpen, isCreatePlaylistDialogOpen, sortOrder
                ->
                if (playlists.isEmpty()) {
                    PlaylistListState.Empty(
                        isCreatePlaylistDialogOpen,
                        isPlaylistCreationErrorDialogOpen,
                    )
                } else {
                    PlaylistListState.Data(
                        playlists =
                            playlists.map { playlist -> PlaylistRow.State.fromPlaylist(playlist) },
                        sortButtonState =
                            SortButton.State(text = RString.playlist_name, sortOrder = sortOrder),
                        isCreatePlaylistDialogOpen = isCreatePlaylistDialogOpen,
                        isPlaylistCreationErrorDialogOpen = isPlaylistCreationErrorDialogOpen,
                    )
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, PlaylistListState.Loading)

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.SortByClicked -> {
                stateHolderScope.launch { sortPreferencesRepository.togglePlaylistListSortOder() }
            }
            is PlaylistListUserAction.CreatePlaylistButtonClicked -> {
                playlistRepository
                    .createPlaylist(action.playlistName)
                    .onEach { playlistId ->
                        if (playlistId == null) {
                            isPlayListCreationErrorDialogOpen.update { true }
                            isCreatePlaylistDialogOpen.update { false }
                        } else {
                            isCreatePlaylistDialogOpen.update { false }
                            navController.push(
                                PlaylistDetailsUiComponent(
                                    arguments =
                                        PlaylistDetailsArguments(
                                            playlistId = playlistId,
                                            playlistName = action.playlistName,
                                        ),
                                    navController = navController,
                                )
                            )
                        }
                    }
                    .launchIn(stateHolderScope)
            }
            is PlaylistListUserAction.DismissPlaylistCreationErrorDialog -> {
                isPlayListCreationErrorDialogOpen.update { false }
            }
            is PlaylistListUserAction.PlaylistMoreIconClicked -> {
                navController.push(
                    PlaylistContextMenu(
                        arguments = PlaylistContextMenuArguments(playlistId = action.playlistId),
                        delegate =
                            object : PlaylistContextMenuDelegate {
                                override fun deletePlaylist() {
                                    navController.pop()
                                    stateHolderScope.launch {
                                        playlistRepository.deletePlaylist(action.playlistId)
                                    }
                                }
                            },
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistListUserAction.CreateNewPlaylistButtonClicked -> {
                isCreatePlaylistDialogOpen.update { true }
            }
            is PlaylistListUserAction.DismissPlaylistCreationDialog -> {
                isCreatePlaylistDialogOpen.update { false }
            }
            is PlaylistListUserAction.PlaylistClicked -> {
                navController.push(
                    PlaylistDetailsUiComponent(
                        arguments =
                            PlaylistDetailsArguments(
                                playlistId = action.playlistId,
                                playlistName = action.playlistName,
                            ),
                        navController = navController,
                    )
                )
            }
        }
    }
}
