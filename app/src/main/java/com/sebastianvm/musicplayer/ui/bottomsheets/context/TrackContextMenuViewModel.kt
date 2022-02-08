package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
    val albumId: String,
    val artistName: String,
    val mediaGroup: MediaGroup,
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialTrackContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialTrackContextMenuStateProvider(savedStateHandle: SavedStateHandle): TrackContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        return TrackContextMenuState(
            mediaId = mediaId,
            menuTitle = "",
            albumId = "",
            artistName = "",
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(),
        )
    }
}

@HiltViewModel
class TrackContextMenuViewModel @Inject constructor(
    initialState: TrackContextMenuState,
    trackRepository: TrackRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<TrackContextMenuState>(initialState) {
    init {
        collect(trackRepository.getTrack(state.value.mediaId)) {
            setState {
                copy(
                    menuTitle = it.track.trackName,
                    listItems = if (state.value.mediaGroup.mediaGroupType == MediaGroupType.ALBUM) {
                        listOf(
                            ContextMenuItem.Play,
                            ContextMenuItem.AddToQueue,
                            if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        )
                    } else {
                        listOf(
                            ContextMenuItem.Play,
                            ContextMenuItem.AddToQueue,
                            if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                            ContextMenuItem.ViewAlbum
                        )
                    },
                    albumId = it.album.albumId,
                    artistName = if (it.artists.size == 1) it.artists[0].artistName else ""
                )
            }
        }
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.Play -> {
                with(state.value) {
                    viewModelScope.launch {
                        mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
                        mediaPlaybackRepository.playFromId(mediaId, mediaGroup)
                        addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                    }
                }
            }
            ContextMenuItem.AddToQueue -> {
                viewModelScope.launch {
                    val didAddToQueue =
                        mediaQueueRepository.addToQueue(listOf(state.value.mediaId))
                    addUiEvent(
                        BaseContextMenuUiEvent.ShowToast(
                            message = if (didAddToQueue) R.string.added_to_queue else R.string.no_queue_available,
                            success = didAddToQueue
                        )
                    )
                }
            }
            ContextMenuItem.ViewAlbum -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToAlbum(state.value.albumId))
            }
            ContextMenuItem.ViewArtist -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtist(
                        state.value.artistName
                    )
                )
            }
            ContextMenuItem.ViewArtists -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtistsBottomSheet(
                        state.value.mediaId,
                        MediaType.TRACK
                    )
                )
            }
            else -> throw IllegalStateException("Invalid row for track context menu")
        }
    }
}