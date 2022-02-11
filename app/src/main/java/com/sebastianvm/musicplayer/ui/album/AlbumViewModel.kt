package com.sebastianvm.musicplayer.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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

// TODO maybe trigger playback from mediaQueueRepo?
@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    trackRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseViewModel<AlbumUiEvent, AlbumState>(initialState) {

    init {
        collect(
            albumRepository.getAlbum(state.value.albumId)
                .combine(trackRepository.getTracksForAlbum(albumId = state.value.albumId)) { albumInfo, tracks ->
                    Pair(albumInfo.album, tracks)
                }) { (album, tracks) ->
            setState {
                copy(
                    imageUri = UriUtils.getAlbumUri(album.albumId.toLong()),
                    albumName = album.albumName,
                    tracksList = tracks.map { it.toTrackRowState(includeTrackNumber = true) }
                        .sortedBy { it.trackNumber }
                )
            }
        }
    }

    fun onTrackClicked(trackId: String) {
        viewModelScope.launch {
            val mediaGroup = MediaGroup(
                mediaGroupType = MediaGroupType.ALBUM,
                mediaId = state.value.albumId
            )
            mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
            mediaPlaybackRepository.playFromId(trackId, mediaGroup)
            addUiEvent(AlbumUiEvent.NavigateToPlayer)
        }
    }

    fun onTrackOverflowMenuIconClicked(trackId: String) {
        addUiEvent(
            AlbumUiEvent.OpenContextMenu(
                trackId = trackId,
                albumId = state.value.albumId
            )
        )
    }
}

data class AlbumState(
    val albumId: String,
    val imageUri: String,
    val albumName: String,
    val tracksList: List<TrackRowState>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedHandle: SavedStateHandle): AlbumState {
        val albumId = savedHandle.get<String>(NavArgs.ALBUM_ID)!!
        return AlbumState(
            albumId = albumId,
            imageUri = "",
            albumName = "",
            tracksList = emptyList()
        )
    }
}

object AlbumUserAction : UserAction

sealed class AlbumUiEvent : UiEvent {
    object NavigateToPlayer : AlbumUiEvent()
    data class OpenContextMenu(val trackId: String, val albumId: String) : AlbumUiEvent()
}
