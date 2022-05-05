package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
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
class PlaylistContextMenuViewModel @Inject constructor(
    initialState: PlaylistContextMenuState,
    private val playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<PlaylistContextMenuState>(initialState) {

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playPlaylist(state.value.playlistName).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                    }
                }.launchIn(viewModelScope)
            }
            is ContextMenuItem.ViewPlaylist -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToPlaylist(playlistName = state.value.playlistName))
            }
            is ContextMenuItem.DeletePlaylist -> {
                setState {
                    copy(
                        showDeleteConfirmationDialog = true
                    )
                }
            }
            else -> throw IllegalStateException("Invalid row for playlist context menu")
        }
    }

    fun onConfirmDeleteClicked() {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(state.value.playlistName)
            setState {
                copy(
                    showDeleteConfirmationDialog = false
                )
            }
        }.invokeOnCompletion {
            addUiEvent(BaseContextMenuUiEvent.HideBottomSheet)
        }
    }

    fun onCancelDeleteClicked() {
        setState {
            copy(
                showDeleteConfirmationDialog = false
            )
        }
    }

    override fun onPlaybackErrorDismissed() {
        setState { copy(playbackResult = null) }
    }
}


data class PlaylistContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
    val playlistName: String,
    val mediaGroup: MediaGroup,
    val showDeleteConfirmationDialog: Boolean,
) : BaseContextMenuState(listItems, menuTitle, playbackResult = playbackResult)


@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistContextMenuStateProvider(savedStateHandle: SavedStateHandle): PlaylistContextMenuState {
        val playlistName = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        return PlaylistContextMenuState(
            playlistName = playlistName,
            menuTitle = playlistName,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewPlaylist,
                ContextMenuItem.DeletePlaylist
            ),
            showDeleteConfirmationDialog = false,
        )
    }
}


