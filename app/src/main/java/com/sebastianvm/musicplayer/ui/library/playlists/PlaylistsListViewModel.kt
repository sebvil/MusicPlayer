package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.playlist.PlaylistArguments
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        preferencesRepository.getPlaylistsListSortOrder().flatMapLatest {
            setState {
                copy(
                    sortOrder = it
                )
            }
            playlistRepository.getPlaylists(it)
        }.onEach { playlists ->
            setState {
                copy(
                    playlistsList = playlists,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onPlaylistClicked(playlistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.Playlist(
                    PlaylistArguments(playlistId = playlistId)
                )
            )
        )
    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyPlaylistsListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addNavEvent(NavEvent.NavigateUp)
    }

    fun onOverflowMenuIconClicked(playlistId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.PlaylistContextMenu(
                    PlaylistContextMenuArguments(playlistId = playlistId)
                )
            )
        )
    }

    fun onFabClicked() {
        setState {
            copy(isDialogOpen = true)
        }
    }

    fun onPlaylistCreated(playlistName: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(playlistName)
        }
        setState {
            copy(isDialogOpen = false)
        }
    }

    fun onDialogDismissed() {
        setState {
            copy(isDialogOpen = false)
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

sealed class PlaylistsListUiEvent : UiEvent