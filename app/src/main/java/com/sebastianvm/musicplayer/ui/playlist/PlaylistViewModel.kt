package com.sebastianvm.musicplayer.ui.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class PlaylistViewModel @Inject constructor(
    initialState: PlaylistState,
    playlistRepository: PlaylistRepository,
    private val playbackManager: PlaybackManager
) : BaseViewModel<PlaylistUiEvent, PlaylistState>(initialState) {

    init {
        playlistRepository.getPlaylistWithTracks(state.value.playlistId).onEach {
            setState {
                copy(playlistName = it.playlist.playlistName,
                    trackList = it.tracks.map { track -> track.toTrackRowState(includeTrackNumber = false) })
            }
        }.launchIn(viewModelScope)
    }

    fun onAddTracksClicked() {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackSearch(
                    TrackSearchArguments(state.value.playlistId)
                )
            )
        )
    }

    fun onTrackClicked(trackIndex: Int) {
        val playTracksFlow =
            playbackManager.playPlaylist(state.value.playlistId, initialTrackIndex = trackIndex)
        playTracksFlow.onEach {
            when (it) {
                is PlaybackResult.Loading, is PlaybackResult.Error -> setState { copy(playbackResult = it) }
                is PlaybackResult.Success -> {
                    setState { copy(playbackResult = it) }
                    addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                }
            }
        }.launchIn(viewModelScope)

    }

    fun onSortByClicked() {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.SortMenu(
                    SortMenuArguments(
                        listType = SortableListType.Playlist,
                        mediaId = state.value.playlistId
                    )
                )
            )
        )
    }

    fun onTrackOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
        addNavEvent(
            NavEvent.NavigateToScreen(
                NavigationDestination.TrackContextMenu(
                    TrackContextMenuArguments(
                        trackId = trackId,
                        mediaType = MediaType.TRACK,
                        mediaGroup = MediaGroup(
                            mediaGroupType = MediaGroupType.PLAYLIST,
                            mediaId = state.value.playlistId
                        ),
                        trackIndex = trackIndex
                    )
                )
            )
        )
    }

    fun onUpButtonClicked() {
        addNavEvent(NavEvent.NavigateUp)
    }

    fun onClosePlaybackErrorDialog() {
        setState { copy(playbackResult = null) }
    }
}

data class PlaylistState(
    val playlistId: Long,
    val playlistName: String,
    val trackList: List<TrackRowState>,
    val playbackResult: PlaybackResult?
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistStateProvider(savedStateHandle: SavedStateHandle): PlaylistState {
        val arguments = savedStateHandle.getArgs<PlaylistArguments>()
        return PlaylistState(
            playlistId = arguments.playlistId,
            playlistName = "",
            trackList = listOf(),
            playbackResult = null
        )
    }
}

sealed class PlaylistUiEvent : UiEvent
