package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.playlist.PlaylistArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistListViewModel @Inject constructor(
    initialState: PlaylistListState,
    private val playlistRepository: PlaylistRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<PlaylistListUiEvent, PlaylistListState>(initialState),
    ViewModelInterface<PlaylistListState, PlaylistListUserAction> {

    init {
        playlistRepository.getPlaylists().onEach { playlists ->
            setState {
                copy(
                    playlistsList = playlists.map { it.toModelListItemState() },
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.PlaylistClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Playlist(
                            PlaylistArguments(playlistId = action.playlistId)
                        )
                    )
                )
            }
            is PlaylistListUserAction.PlaylistOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.PlaylistContextMenu(
                            PlaylistContextMenuArguments(playlistId = action.playlistId)
                        )
                    )
                )
            }
            is PlaylistListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyPlaylistsListSortOrder()
                }
            }
            is PlaylistListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
            is PlaylistListUserAction.AddPlaylistButtonClicked -> {
                setState {
                    copy(isCreatePlaylistDialogOpen = true)
                }
            }
            is PlaylistListUserAction.CreatePlaylistButtonClicked -> {
                playlistRepository.createPlaylist(action.playlistName).onEach {
                    if (it == null) {
                        setState {
                            copy(
                                isPlaylistCreationErrorDialogOpen = true,
                                isCreatePlaylistDialogOpen = false
                            )
                        }
                    } else {
                        setState {
                            copy(isCreatePlaylistDialogOpen = false)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is PlaylistListUserAction.DismissPlaylistCreationButtonClicked -> {
                setState {
                    copy(isCreatePlaylistDialogOpen = false)
                }
            }
            is PlaylistListUserAction.DismissPlaylistCreationErrorDialog -> {
                setState {
                    copy(isPlaylistCreationErrorDialogOpen = false)
                }
            }
        }
    }
}

data class PlaylistListState(
    val playlistsList: List<ModelListItemState>,
    val isCreatePlaylistDialogOpen: Boolean,
    val isPlaylistCreationErrorDialogOpen: Boolean
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistsListStateModule {

    @Provides
    @ViewModelScoped
    fun initialPlaylistsListStateProvider() =
        PlaylistListState(
            playlistsList = listOf(),
            isCreatePlaylistDialogOpen = false,
            isPlaylistCreationErrorDialogOpen = false
        )
}

sealed interface PlaylistListUiEvent : UiEvent
sealed interface PlaylistListUserAction : UserAction {
    data class PlaylistClicked(val playlistId: Long) : PlaylistListUserAction
    data class PlaylistOverflowMenuIconClicked(val playlistId: Long) : PlaylistListUserAction
    object SortByButtonClicked : PlaylistListUserAction
    object UpButtonClicked : PlaylistListUserAction
    object AddPlaylistButtonClicked : PlaylistListUserAction
    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction
    object DismissPlaylistCreationButtonClicked : PlaylistListUserAction
    object DismissPlaylistCreationErrorDialog : PlaylistListUserAction
}