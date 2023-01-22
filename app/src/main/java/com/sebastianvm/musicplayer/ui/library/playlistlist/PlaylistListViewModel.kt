package com.sebastianvm.musicplayer.ui.library.playlistlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
    private val sortPreferencesRepository: SortPreferencesRepository,
) : BaseViewModel<PlaylistListState, PlaylistListUserAction>(initialState) {

    init {
        playlistRepository.getPlaylists().onEach { playlists ->
            setState {
                copy(
                    playlistList = playlists.map { it.toModelListItemState() },
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: PlaylistListUserAction) {
        when (action) {
            is PlaylistListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    sortPreferencesRepository.togglePlaylistListSortOder()
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

            is PlaylistListUserAction.DismissPlaylistCreationErrorDialog -> {
                setState {
                    copy(isPlaylistCreationErrorDialogOpen = false)
                }
            }
        }
    }
}

data class PlaylistListState(
    val playlistList: List<ModelListItemState>,
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
            playlistList = listOf(),
            isCreatePlaylistDialogOpen = false,
            isPlaylistCreationErrorDialogOpen = false
        )
}

sealed interface PlaylistListUserAction : UserAction {
    object SortByClicked : PlaylistListUserAction
    data class CreatePlaylistButtonClicked(val playlistName: String) : PlaylistListUserAction
    object DismissPlaylistCreationErrorDialog : PlaylistListUserAction
}
