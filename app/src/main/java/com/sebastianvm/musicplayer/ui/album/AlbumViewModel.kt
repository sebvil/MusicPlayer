package com.sebastianvm.musicplayer.ui.album

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.bottomsheets.context.ContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    trackRepository: TrackRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<AlbumUiEvent, AlbumState>(initialState) {

    init {
        combine(
            albumRepository.getAlbum(state.value.albumId),
            trackRepository.getTracksForAlbum(state.value.albumId)
        ) { albumInfo, tracks ->
            Pair(albumInfo.album, tracks)
        }.onEach { (album, tracks) ->
            setState {
                copy(
                    imageUri = UriUtils.getAlbumUri(album.id),
                    albumName = album.albumName,
                    trackList = tracks.map { it.toTrackRowState(includeTrackNumber = true) }
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onTrackClicked(trackIndex: Int) {
        viewModelScope.launch {
            playbackManager.playAlbum(albumId = state.value.albumId, initialTrackIndex = trackIndex)
            addUiEvent(AlbumUiEvent.NavEvent(NavigationDestination.MusicPlayer))
        }
    }

    fun onTrackOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
        addUiEvent(
            AlbumUiEvent.NavEvent(
                NavigationDestination.ContextMenu(
                    ContextMenuArguments(
                        mediaId = trackId,
                        mediaType = MediaType.TRACK,
                        mediaGroup = MediaGroup(
                            mediaId = state.value.albumId,
                            mediaGroupType = MediaGroupType.ALBUM
                        ),
                        trackIndex = trackIndex
                    )
                )
            )
        )
    }
}

data class AlbumState(
    val albumId: Long,
    val imageUri: Uri,
    val albumName: String,
    val trackList: List<TrackRowState>,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedStateHandle: SavedStateHandle): AlbumState {
        val args = savedStateHandle.getArgs<AlbumArguments>()
        return AlbumState(
            albumId = args.albumId,
            imageUri = Uri.EMPTY,
            albumName = "",
            trackList = emptyList(),
        )
    }
}

sealed class AlbumUiEvent : UiEvent {
    data class NavEvent(val navigationDestination: NavigationDestination) : AlbumUiEvent()
}
