package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.destinations.TrackListRouteDestination
import com.sebastianvm.musicplayer.ui.library.tracklist.TrackListArgumentsForNav
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlaylistContextMenuViewModel(
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
