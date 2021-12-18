package com.sebastianvm.musicplayer.ui.album

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.player.PARENT_ID
import com.sebastianvm.musicplayer.player.SORT_BY
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.components.HeaderWithImageState
import com.sebastianvm.musicplayer.ui.components.TrackRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    trackRepository: TrackRepository,
    private val musicServiceConnection: MusicServiceConnection,
) : BaseViewModel<AlbumUserAction, AlbumUiEvent, AlbumState>(initialState) {

    init {
        viewModelScope.launch {
            albumRepository.getAlbums(state.value.albumGid).collect { albumInfo ->
                setState {
                    copy(
                        albumHeaderItem = HeaderWithImageState(
                            image = ArtLoader.getAlbumArt(
                                albumGid = albumInfo.album.albumGid.toLong(),
                                albumName = albumInfo.album.albumName
                            ),
                            title = albumInfo.album.albumName.let {
                                if (it.isNotEmpty()) DisplayableString.StringValue(
                                    it
                                ) else DisplayableString.ResourceValue(com.sebastianvm.musicplayer.R.string.unknown_album)
                            }
                        ),
                    )
                }
                trackRepository.getTracks(albumInfo.tracks.map { it.trackGid }).collect { tracks ->
                    setState {
                        copy(
                            tracksList = tracks.map { it.toTrackRowState() }
                        )
                    }
                }
            }
        }
    }

    private fun FullTrackInfo.toTrackRowState(): TrackRowState {
        return TrackRowState(
            trackGid = track.trackGid,
            trackName = track.trackName,
            artists = artists.joinToString(", ") { it.artistName },
            albumName = album.albumName,
            trackNumber = track.trackNumber
        )
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
    val tracksList: List<TrackRowState>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumStateModule {
    @Provides
    @ViewModelScoped
    fun provideInitialAlbumState(savedHandle: SavedStateHandle): AlbumState {
        val albumGid = savedHandle.get<String>(NavArgs.ALBUM_GID)!!
        return AlbumState(
            albumGid = albumGid,
            albumHeaderItem = HeaderWithImageState(
                image = ArtLoader.getAlbumArt(
                    albumGid = albumGid.toLong(),
                    albumName = ""
                ),
                title = DisplayableString.ResourceValue(com.sebastianvm.musicplayer.R.string.unknown_album)
            ),
            tracksList = emptyList()
        )
    }
}

sealed class AlbumUserAction : UserAction {
    data class TrackClicked(val trackGid: String) : AlbumUserAction()
}

sealed class AlbumUiEvent : UiEvent {
    object NavigateToPlayer : AlbumUiEvent()
}
