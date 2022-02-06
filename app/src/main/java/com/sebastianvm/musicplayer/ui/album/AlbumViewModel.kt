package com.sebastianvm.musicplayer.ui.album

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.launchViewModelIOScope
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseViewModel<AlbumUserAction, AlbumUiEvent, AlbumState>(initialState) {

    init {
        collect(albumRepository.getAlbumWithTracks(state.value.albumId)) { albumInfo ->
            val album = albumInfo.keys.find { it.albumId == state.value.albumId }
            album?.also {
                setState {
                    copy(
                        imageUri = UriUtils.getAlbumUri(album.albumId.toLong()),
                        albumName = album.albumName,
                        tracksList = albumInfo[album]?.map { it.toTrackRowState(includeTrackNumber = true) }
                            ?.sortedBy { it.trackNumber } ?: listOf()
                    )
                }
            }

        }
    }

    override fun handle(action: AlbumUserAction) {
        when (action) {
            is AlbumUserAction.TrackClicked -> {
                launchViewModelIOScope {
                    val mediaGroup = MediaGroup(
                        mediaGroupType = MediaGroupType.ALBUM,
                        mediaId = state.value.albumId
                    )
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = SortOrder.ASCENDING,
                        sortOption = SortOption.TRACK_NUMBER
                    )
                    mediaPlaybackRepository.playFromId(action.trackId, mediaGroup)
                    addUiEvent(AlbumUiEvent.NavigateToPlayer)
                }
            }
            is AlbumUserAction.TrackContextMenuClicked -> {
                addUiEvent(
                    AlbumUiEvent.OpenContextMenu(
                        trackId = action.trackId,
                        albumId = state.value.albumId
                    )
                )
            }
        }
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

sealed class AlbumUserAction : UserAction {
    data class TrackClicked(val trackId: String) : AlbumUserAction()
    data class TrackContextMenuClicked(val trackId: String) : AlbumUserAction()
}

sealed class AlbumUiEvent : UiEvent {
    object NavigateToPlayer : AlbumUiEvent()
    data class OpenContextMenu(val trackId: String, val albumId: String) : AlbumUiEvent()
}
