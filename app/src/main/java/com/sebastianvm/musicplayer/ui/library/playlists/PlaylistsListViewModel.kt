package com.sebastianvm.musicplayer.ui.library.playlists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.database.entities.Playlist
import com.sebastianvm.musicplayer.player.TracksListType
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.getStringComparator
import com.sebastianvm.musicplayer.util.not
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsListViewModel @Inject constructor(
    initialState: PlaylistsListState,
    private val playlistRepository: PlaylistRepository,
    private val preferencesRepository: PreferencesRepository,
) :
    BaseViewModel<PlaylistsListUserAction, PlaylistsListUiEvent, PlaylistsListState>(initialState) {

    init {
        collect(
            preferencesRepository.getPlaylistsListSortOrder()
                .combine(playlistRepository.getPlaylists()) { sortOrder, playlistsList ->
                    Pair(sortOrder, playlistsList)
                }) { pair ->
            setState {
                copy(
                    sortOrder = pair.first,
                    playlistsList = pair.second.sortedWith(getStringComparator(pair.first) { item -> item.playlistName }),
                )
            }
        }
    }

    override fun handle(action: PlaylistsListUserAction) {
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
                viewModelScope.launch {
                    val sortSettings =
                        preferencesRepository.getTracksListSortOptions(TracksListType.PLAYLIST, action.playlistName).first()
                    addUiEvent(
                        PlaylistsListUiEvent.OpenContextMenu(
                            action.playlistName,
                            sortSettings.sortOption,
                            sortSettings.sortOrder
                        )
                    )

                }
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
            isDialogOpen = false
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
    data class OpenContextMenu(
        val playlistName: String,
        val currentSort: MediaSortOption,
        val sortOrder: MediaSortOrder
    ) : PlaylistsListUiEvent()
}
