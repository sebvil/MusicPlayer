package com.sebastianvm.musicplayer.features.playlist.list

import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.playlistDetails
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.api.playlist.menu.playlistContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
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
    private val features: FeatureRegistry,
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
                                features
                                    .playlistDetails()
                                    .playlistDetailsUiComponent(
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
                    features
                        .playlistContextMenu()
                        .playlistContextMenuUiComponent(
                            arguments =
                                PlaylistContextMenuArguments(playlistId = action.playlistId),
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
                    features
                        .playlistDetails()
                        .playlistDetailsUiComponent(
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
