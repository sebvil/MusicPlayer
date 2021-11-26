package com.sebastianvm.musicplayer.ui.album

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
import com.sebastianvm.musicplayer.util.extensions.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    initialState: AlbumState
) : BaseViewModel<AlbumUserAction, AlbumUiEvent, AlbumState>(
    initialState
) {

    init {

        musicServiceConnection.subscribe(
            "album-${state.value.albumGid}",
            object : MediaBrowserCompat.SubscriptionCallback() {
                override fun onChildrenLoaded(
                    parentId: String,
                    children: MutableList<MediaBrowserCompat.MediaItem>
                ) {
                    super.onChildrenLoaded(parentId, children)
                    setState {
                        copy(
                            albumAdapterItems = children.mapNotNull { child -> child.description.toTrackRowState() }
                                .sortedBy { it.trackNumber }
                        )
                    }
                }
            }
        )
    }

    fun MediaDescriptionCompat.toTrackRowState(): TrackRowState? {
        val meta =
            extras?.getParcelable<MediaMetadataCompat>(MEDIA_METADATA_COMPAT_KEY) ?: return null
        val trackGid = meta.id ?: return null
        val trackName = meta.title ?: return null
        val artists = meta.artist ?: return null
        val trackNumber = meta.trackNumber
        return TrackRowState(trackGid, trackName, artists, trackNumber)
    }

    override fun handle(action: AlbumUserAction) {
        when (action) {
            is AlbumUserAction.TrackClicked -> {
                val transportControls = musicServiceConnection.transportControls
                val extras = Bundle().apply {
                    putString(
                        PARENT_ID,
                        "album-${state.value.albumGid}"
                    )
                    putString(
                        SORT_BY,
                        MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER
                    )
                }

                transportControls.playFromMediaId(action.trackGid, extras)
                addUiEvent(AlbumUiEvent.NavigateToPlayer)
            }
        }
    }

}

data class AlbumState(
    val albumGid: String,
    val albumHeaderItem: HeaderWithImageState,
    val albumAdapterItems: List<TrackRowState>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedHandle: SavedStateHandle): AlbumState {
        val albumGid = savedHandle.get<String>(NavArgs.ALBUM_GID)!!
        val albumName = savedHandle.get<String>(NavArgs.ALBUM_NAME)

        return AlbumState(
            albumGid = albumGid,
            albumHeaderItem = HeaderWithImageState(
                image = ArtLoader.getAlbumArt(
                    albumGid = albumGid.toLong(),
                    albumName = albumName ?: ""
                ),
                title = albumName?.let { DisplayableString.StringValue(it) }
                    ?: DisplayableString.ResourceValue(com.sebastianvm.musicplayer.R.string.unknown_album)
            ),
            albumAdapterItems = emptyList()
        )
    }
}

sealed class AlbumUserAction : UserAction {
    data class TrackClicked(val trackGid: String) : AlbumUserAction()
}

sealed class AlbumUiEvent : UiEvent {
    object NavigateToPlayer : AlbumUiEvent()
}
