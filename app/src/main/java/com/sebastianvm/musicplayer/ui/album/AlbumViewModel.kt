package com.sebastianvm.musicplayer.ui.album

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.player.MEDIA_GROUP
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.repository.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.components.toTrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val musicServiceConnection: MusicServiceConnection,
) : BaseViewModel<AlbumUserAction, AlbumUiEvent, AlbumState>(initialState) {

    init {
        collect(albumRepository.getAlbumWithTracks(state.value.albumId)) { albumInfo ->
            val album = albumInfo.keys.find { it.albumId == state.value.albumId }
            album?.also {
                setState {
                    copy(
                        albumHeaderItem = HeaderWithImageState(
                            image = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, album.albumId.toLong()),
                            title = album.albumName.let {
                                if (it.isNotEmpty()) DisplayableString.StringValue(
                                    it
                                ) else DisplayableString.ResourceValue(com.sebastianvm.musicplayer.R.string.unknown_album)
                            }
                        ),
                        tracksList = albumInfo[album]?.map { it.toTrackRowState() }?.sortedBy { it.trackNumber } ?: listOf()
                    )
                }
            }

        }
    }

    override fun handle(action: AlbumUserAction) {
        when (action) {
            is AlbumUserAction.TrackClicked -> {
                val transportControls = musicServiceConnection.transportControls
                viewModelScope.launch {
                    val mediaGroup =  MediaGroup(
                        mediaType = MediaType.ALBUM,
                        mediaId = state.value.albumId
                    )
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = SortOrder.ASCENDING,
                        sortOption = SortOption.TRACK_NUMBER
                    )
                    val extras = Bundle().apply {
                        putParcelable(MEDIA_GROUP, mediaGroup)
                    }
                    transportControls.playFromMediaId(action.trackId, extras)
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
    val albumHeaderItem: HeaderWithImageState,
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
            albumHeaderItem = HeaderWithImageState(
                image = Uri.EMPTY,
                title = DisplayableString.ResourceValue(com.sebastianvm.musicplayer.R.string.unknown_album)
            ),
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
