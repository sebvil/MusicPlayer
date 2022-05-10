package com.sebastianvm.musicplayer.ui.album

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
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
        collect(
            albumRepository.getAlbum(state.value.albumId)
                .combine(trackRepository.getTracksForAlbum(albumId = state.value.albumId)) { albumInfo, tracks ->
                    Pair(albumInfo.album, tracks)
                }) { (album, tracks) ->
            setState {
                copy(
                    imageUri = UriUtils.getAlbumUri(album.id),
                    albumName = album.albumName,
                    tracksList = tracks.map { it.toTrackRowState(includeTrackNumber = true) }
                )
            }
        }
    }

    fun onTrackClicked(trackIndex: Int) {
        viewModelScope.launch {
            playbackManager.playAlbum(albumId = state.value.albumId, initialTrackIndex = trackIndex)
            addUiEvent(AlbumUiEvent.NavigateToPlayer)
        }
    }

    fun onTrackOverflowMenuIconClicked(trackIndex: Int, trackId: Long) {
        addUiEvent(
            AlbumUiEvent.OpenContextMenu(
                trackId = trackId,
                albumId = state.value.albumId,
                trackIndex = trackIndex
            )
        )
    }
}

data class AlbumState(
    val albumId: Long,
    val imageUri: Uri,
    val albumName: String,
    val tracksList: List<TrackRowState>,
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedHandle: SavedStateHandle): AlbumState {
        val albumId = savedHandle.get<Long>(NavArgs.ALBUM_ID)!!
        return AlbumState(
            albumId = albumId,
            imageUri = Uri.EMPTY,
            albumName = "",
            tracksList = emptyList(),
        )
    }
}

sealed class AlbumUiEvent : UiEvent {
    object NavigateToPlayer : AlbumUiEvent()
    data class OpenContextMenu(val trackId: Long, val albumId: Long, val trackIndex: Int) : AlbumUiEvent()
}
