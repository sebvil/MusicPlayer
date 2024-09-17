package com.sebastianvm.musicplayer.features.playlist.list

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.PlaylistRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.designsystems.components.TextFieldDialog
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsProps
import com.sebastianvm.musicplayer.features.api.playlist.details.playlistDetails
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListProps
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuProps
import com.sebastianvm.musicplayer.features.api.playlist.menu.playlistContextMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PlaylistListState : State {

    val createPlaylistDialogState: TextFieldDialog.State?

    data class Data(
        val playlists: List<PlaylistRow.State>,
        val sortButtonState: SortButton.State,
        override val createPlaylistDialogState: TextFieldDialog.State?,
    ) : PlaylistListState

    data class Empty(override val createPlaylistDialogState: TextFieldDialog.State?) :
        PlaylistListState

    data object Loading : PlaylistListState {
        override val createPlaylistDialogState: TextFieldDialog.State? = null
    }
}

sealed interface PlaylistListUserAction : UserAction {
    data object SortByClicked : PlaylistListUserAction

    data class PlaylistMoreIconClicked(val playlistId: Long) : PlaylistListUserAction

    data class PlaylistClicked(val playlistId: Long, val playlistName: String) :
        PlaylistListUserAction

    data object CreateNewPlaylistButtonClicked : PlaylistListUserAction

    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction

    data object DismissPlaylistCreationDialog : PlaylistListUserAction
}

class PlaylistListViewModel(
    viewModelScope: CoroutineScope = getViewModelScope(),
    private val playlistRepository: PlaylistRepository,
    private val sortPreferencesRepository: SortPreferencesRepository,
    private val props: StateFlow<PlaylistListProps>,
    private val features: FeatureRegistry,
) : BaseViewModel<PlaylistListState, PlaylistListUserAction>(viewModelScope = viewModelScope) {

    private val navController: NavController
        get() = props.value.navController

    private val _playlistCreationDialogState: MutableStateFlow<TextFieldDialog.State?> =
        MutableStateFlow(null)
    private val sortOrder = sortPreferencesRepository.getPlaylistsListSortOrder()

    override val state: StateFlow<PlaylistListState> =
        combine(playlistRepository.getPlaylists(), _playlistCreationDialogState, sortOrder) {
                playlists,
                isCreatePlaylistDialogOpen,
                sortOrder ->
                if (playlists.isEmpty()) {
                    PlaylistListState.Empty(isCreatePlaylistDialogOpen)
                } else {
                    PlaylistListState.Data(
                        playlists =
                            playlists.map { playlist -> PlaylistRow.State.fromPlaylist(playlist) },
                        sortButtonState =
                            SortButton.State(text = RString.playlist_name, sortOrder = sortOrder),
                        createPlaylistDialogState = isCreatePlaylistDialogOpen,
                    )
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                PlaylistListState.Loading,
            )

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.SortByClicked -> {
                viewModelScope.launch { sortPreferencesRepository.togglePlaylistListSortOder() }
            }
            is PlaylistListUserAction.CreatePlaylistButtonClicked -> {
                if (action.playlistName.isBlank()) {
                    _playlistCreationDialogState.update {
                        it?.copy(errorMessage = RString.playlist_name_cannot_be_empty)
                    }
                    return
                }
                viewModelScope.launch {
                    val playlistId = playlistRepository.createPlaylist(action.playlistName)

                    _playlistCreationDialogState.value = null
                    navController.push(
                        features
                            .playlistDetails()
                            .create(
                                arguments =
                                    PlaylistDetailsArguments(
                                        playlistId = playlistId,
                                        playlistName = action.playlistName,
                                    ),
                                props =
                                    MutableStateFlow(
                                        PlaylistDetailsProps(navController = navController)
                                    ),
                            )
                    )
                }
            }
            is PlaylistListUserAction.PlaylistMoreIconClicked -> {
                navController.push(
                    features
                        .playlistContextMenu()
                        .create(
                            arguments =
                                PlaylistContextMenuArguments(playlistId = action.playlistId),
                            props =
                                MutableStateFlow(
                                    PlaylistContextMenuProps(
                                        deletePlaylist = {
                                            navController.pop()
                                            viewModelScope.launch {
                                                playlistRepository.deletePlaylist(action.playlistId)
                                            }
                                        }
                                    )
                                ),
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is PlaylistListUserAction.CreateNewPlaylistButtonClicked -> {
                _playlistCreationDialogState.value =
                    TextFieldDialog.State(
                        title = RString.playlist_name,
                        confirmButtonText = RString.create,
                        initialText = "",
                        onSave = { handle(PlaylistListUserAction.CreatePlaylistButtonClicked(it)) },
                        onDismiss = { handle(PlaylistListUserAction.DismissPlaylistCreationDialog) },
                    )
            }
            is PlaylistListUserAction.DismissPlaylistCreationDialog -> {
                _playlistCreationDialogState.value = null
            }
            is PlaylistListUserAction.PlaylistClicked -> {
                navController.push(
                    features
                        .playlistDetails()
                        .create(
                            arguments =
                                PlaylistDetailsArguments(
                                    playlistId = action.playlistId,
                                    playlistName = action.playlistName,
                                ),
                            props =
                                MutableStateFlow(
                                    PlaylistDetailsProps(navController = navController)
                                ),
                        )
                )
            }
        }
    }
}
