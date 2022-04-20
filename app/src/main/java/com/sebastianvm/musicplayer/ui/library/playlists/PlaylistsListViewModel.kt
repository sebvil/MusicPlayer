package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistsListViewModel @Inject constructor(
    initialState: PlaylistsListState,
    private val playlistRepository: PlaylistRepository,
    private val preferencesRepository: SortPreferencesRepository,
) :
    BaseViewModel<PlaylistsListUiEvent, PlaylistsListState>(initialState) {

    init {
        viewModelScope.launch {
            preferencesRepository.getPlaylistsListSortOrder().flatMapLatest {
                setState {
                    copy(
                        sortOrder = it
                    )
                }
                playlistRepository.getPlaylists(it)
            }.collect { playlists ->
                setState {
                    copy(
                        playlistsList = playlists,
                    )
                }
            }
        }
    }

    fun <A : UserAction> handle(action: A) {
        when (action) {
            is PlaylistsListUserAction.PlaylistClicked -> {
                addUiEvent(PlaylistsListUiEvent.NavigateToPlaylist(playlistName = action.playlistName))
            }
            is PlaylistsListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyPlaylistsListSortOrder(!state.value.sortOrder)
                }
            }
            is PlaylistsListUserAction.UpButtonClicked -> addUiEvent(PlaylistsListUiEvent.NavigateUp)
            is PlaylistsListUserAction.OverflowMenuIconClicked -> {
                addUiEvent(PlaylistsListUiEvent.OpenContextMenu(action.playlistName))
            }
            is PlaylistsListUserAction.FabClicked -> {
                setState {
                    copy(isDialogOpen = true)
                }
            }
            is PlaylistsListUserAction.DialogDismissed -> {
                setState {
                    copy(isDialogOpen = false)
                }
            }
            is PlaylistsListUserAction.PlaylistCreated -> {
                viewModelScope.launch {
                    playlistRepository.createPlaylist(action.playlistName)
                }
                setState {
                    copy(isDialogOpen = false)
                }
            }
        }
    }
}

data class PlaylistsListState(
    val playlistsList: List<Playlist>,
    val isDialogOpen: Boolean,
    val sortOrder: MediaSortOrder
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistsListStateModule {

    @Provides
    @ViewModelScoped
    fun initialPlaylistsListStateProvider() =
        PlaylistsListState(
            playlistsList = listOf(),
            sortOrder = MediaSortOrder.ASCENDING,
            isDialogOpen = false,
        )
}

sealed class PlaylistsListUserAction : UserAction {
    data class PlaylistClicked(val playlistName: String) : PlaylistsListUserAction()
    object UpButtonClicked : PlaylistsListUserAction()
    object SortByClicked : PlaylistsListUserAction()
    data class OverflowMenuIconClicked(val playlistName: String) : PlaylistsListUserAction()
    object FabClicked : PlaylistsListUserAction()
    object DialogDismissed : PlaylistsListUserAction()
    data class PlaylistCreated(val playlistName: String) : PlaylistsListUserAction()
}

sealed class PlaylistsListUiEvent : UiEvent {
    data class NavigateToPlaylist(val playlistName: String) : PlaylistsListUiEvent()
    object NavigateUp : PlaylistsListUiEvent()
    data class OpenContextMenu(val playlistName: String) : PlaylistsListUiEvent()
}
