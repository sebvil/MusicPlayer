package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumContextMenuViewModel @Inject constructor(
    initialState: AlbumContextMenuState,
    albumRepository: AlbumRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<AlbumContextMenuState>(initialState) {

    private var trackIds: List<String> = listOf()
    private var artistIds: List<String> = listOf()

    init {
        collect(albumRepository.getAlbum(state.value.mediaId)) {
            trackIds = it.tracks
            artistIds = it.artists
            setState {
                copy(
                    menuTitle = it.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (artistIds.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                        ContextMenuItem.ViewAlbum
                    )
                )
            }
        }
    }

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayFromBeginning -> {
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(MediaGroupType.ALBUM, state.value.mediaId)
                    mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
                    playbackManager.playFromId(state.value.mediaId, mediaGroup)
                    addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                }
            }
            is ContextMenuItem.AddToQueue -> {
                viewModelScope.launch {
                    val didAddToQueue =
                        mediaQueueRepository.addToQueue(trackIds)
                    addUiEvent(
                        BaseContextMenuUiEvent.ShowToast(
                            message = if (didAddToQueue) R.string.added_to_queue else R.string.no_queue_available,
                            success = didAddToQueue
                        )
                    )
                }
            }
            is ContextMenuItem.ViewAlbum -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToAlbum(state.value.mediaId))
            }
            is ContextMenuItem.ViewArtists -> {
                addUiEvent(
                    BaseContextMenuUiEvent.NavigateToArtistsBottomSheet(
                        state.value.mediaId,
                        MediaType.ALBUM
                    )
                )
            }
            is ContextMenuItem.ViewArtist -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToArtist(artistIds[0]))
            }
            else -> throw IllegalStateException("Invalid row for album context menu")
        }
    }
}

data class AlbumContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
) : BaseContextMenuState(listItems, menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumContextMenuStateProvider(savedStateHandle: SavedStateHandle): AlbumContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        return AlbumContextMenuState(
            mediaId = mediaId,
            menuTitle = "",
            listItems = listOf(),
        )
    }
}


