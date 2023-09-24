package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
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
    arguments: PlaylistContextMenuArguments,
    private val playlistRepository: PlaylistRepository,
) : BaseContextMenuViewModel() {

    private val playlistId = arguments.playlistId

    init {
        playlistRepository.getPlaylistName(playlistId).onEach { playlistName ->
            setDataState {
                it.copy(menuTitle = playlistName)
            }
        }.launchIn(viewModelScope)
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.ViewPlaylist -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        TrackListRouteDestination(
                            TrackListArgumentsForNav(
                                trackListType = MediaGroup.Playlist(playlistId)
                            )
                        )
                    )
                )
            }

            is ContextMenuItem.DeletePlaylist -> {
                setDataState {
                    it.copy(
                        showDeleteConfirmationDialog = true
                    )
                }
            }

            else -> error("Invalid row for playlist context menu")
        }
    }

    override fun onConfirmDeleteClicked() {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            setDataState {
                it.copy(
                    showDeleteConfirmationDialog = false
                )
            }
        }.invokeOnCompletion {
            addNavEvent(NavEvent.NavigateUp)
        }
    }

    override fun onCancelDeleteClicked() {
        setDataState {
            it.copy(
                showDeleteConfirmationDialog = false
            )
        }
    }

    override fun onPlaybackErrorDismissed() {
        setDataState { it.copy(playbackResult = null) }
    }

    override val defaultState: ContextMenuState by lazy {
        ContextMenuState(
            menuTitle = "",
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewPlaylist,
                ContextMenuItem.DeletePlaylist
            ),
            showDeleteConfirmationDialog = false
        )
    }
}

data class PlaylistContextMenuArguments(val playlistId: Long)

@InstallIn(ViewModelComponent::class)
@Module
object PlaylistContextMenuArgumentsModule {
    @Provides
    @ViewModelScoped
    fun playlistContextMenuArgumentsProvider(savedStateHandle: SavedStateHandle): PlaylistContextMenuArguments {
        return savedStateHandle.navArgs()
    }
}
