package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
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

    init {
        playlistRepository.getPlaylist(state.value.mediaId).onEach {
            it?.also {
                setState {
                    copy(menuTitle = it.playlistName)
                }
            }

        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playPlaylist(state.value.mediaId).onEach {
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
                addUiEvent(BaseContextMenuUiEvent.NavigateToPlaylist(playlistId = state.value.mediaId))
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
            playlistRepository.deletePlaylist(state.value.mediaId)
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
    override val mediaId: Long,
    override val menuTitle: String,
    override val playbackResult: PlaybackResult? = null,
    val showDeleteConfirmationDialog: Boolean,
) : BaseContextMenuState(listItems, mediaId, menuTitle, playbackResult)


@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistContextMenuStateProvider(savedStateHandle: SavedStateHandle): PlaylistContextMenuState {
        val playlistId = savedStateHandle.get<Long>(NavArgs.MEDIA_ID)!!
        return PlaylistContextMenuState(
            mediaId = playlistId,
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewPlaylist,
                ContextMenuItem.DeletePlaylist
            ),
            showDeleteConfirmationDialog = false,
        )
    }
}


