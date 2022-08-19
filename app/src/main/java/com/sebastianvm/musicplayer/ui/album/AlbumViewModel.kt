package com.sebastianvm.musicplayer.ui.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.PlaybackResult
import com.sebastianvm.musicplayer.ui.bottomsheets.context.TrackContextMenuArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    initialState: AlbumState,
    albumRepository: AlbumRepository,
    private val playbackManager: PlaybackManager,
) : BaseViewModel<AlbumUiEvent, AlbumState>(initialState),
    ViewModelInterface<AlbumState, AlbumUserAction> {

    init {
        viewModelScope.launch {
            val albumWithTracks = albumRepository.getAlbumWithTracks(state.value.albumId).first()
            setState {
                copy(
                    imageUri = albumWithTracks.album.imageUri,
                    albumName = albumWithTracks.album.albumName,
                    trackList = albumWithTracks.tracks.sortedBy { it.trackNumber }
                        .map { it.toModelListItemState() }
                )
            }
        }
    }

    override fun handle(action: AlbumUserAction) {
        when (action) {
            is AlbumUserAction.TrackClicked -> {
                playbackManager.playAlbum(
                    albumId = state.value.albumId,
                    initialTrackIndex = action.trackIndex
                ).onEach {
                    when (it) {
                        is PlaybackResult.Loading, is PlaybackResult.Error -> setState {
                            copy(
                                playbackResult = it
                            )
                        }
                        is PlaybackResult.Success -> {
                            setState { copy(playbackResult = null) }
                            addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.MusicPlayer))
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is AlbumUserAction.TrackOverflowMenuIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.TrackContextMenu(
                            TrackContextMenuArguments(
                                trackId = action.trackId,
                                mediaType = MediaType.TRACK,
                                mediaGroup = MediaGroup(
                                    mediaId = state.value.albumId,
                                    mediaGroupType = MediaGroupType.ALBUM
                                ),
                                trackIndex = action.trackIndex
                            )
                        )
                    )
                )
            }
        }
    }

}

data class AlbumState(
    val albumId: Long,
    val imageUri: String,
    val albumName: String,
    val trackList: List<ModelListItemState>,
    val playbackResult: PlaybackResult? = null
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
            imageUri = "",
            albumName = "",
            trackList = listOf(),
        )
    }
}

sealed class AlbumUiEvent : UiEvent
sealed interface AlbumUserAction : UserAction {
    data class TrackClicked(val trackIndex: Int) : AlbumUserAction
    data class TrackOverflowMenuIconClicked(val trackIndex: Int, val trackId: Long) :
        AlbumUserAction
}