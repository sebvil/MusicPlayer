package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArguments
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
        playlistRepository.getPlaylistName(state.mediaId).onEach { playlistName ->
            requireNotNull(playlistName)
            setState {
                copy(menuTitle = playlistName)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                playbackManager.playPlaylist(state.mediaId).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }

                        is PlaybackResult.Success -> {}
                    }
                }.launchIn(viewModelScope)
            }

            is ContextMenuItem.ViewPlaylist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArguments(
                                trackList = MediaGroup.Playlist(state.mediaId)
                            )
                        )
                    )
                )
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

    override fun onConfirmDeleteClicked() {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(state.mediaId)
            setState {
                copy(
                    showDeleteConfirmationDialog = false
                )
            }
        }.invokeOnCompletion {
            addNavEvent(NavEvent.NavigateUp)
        }
    }

    override fun onCancelDeleteClicked() {
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
        val args = savedStateHandle.navArgs<PlaylistContextMenuArguments>()
        return PlaylistContextMenuState(
            mediaId = args.playlistId,
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


