package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
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
    private val albumRepository: AlbumRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<AlbumContextMenuState>(initialState) {

    private var trackIds: List<String> = listOf()

    init {
        collect(albumRepository.getAlbum(state.value.mediaId)) {
            trackIds = it.tracks.map { track -> track.trackId }
            setState {
                copy(
                    menuTitle = it.album.albumName,
                    listItems = listOf(
                        ContextMenuItem.PlayFromBeginning,
                        ContextMenuItem.AddToQueue,
                        if (it.artists.size == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
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
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = state.value.sortOrder,
                        sortOption = MediaSortOption.valueOf(state.value.selectedSort)
                    )
                    mediaPlaybackRepository.playFromId(state.value.mediaId, mediaGroup)
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
                collect(albumRepository.getAlbum(state.value.mediaId)) { album ->
                    addUiEvent(BaseContextMenuUiEvent.NavigateToArtist(album.artists[0].artistName))
                }
            }
            else -> throw IllegalStateException("Invalid row for album context menu")
        }
    }
}

data class AlbumContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val mediaId: String,
    val selectedSort: String,
    val sortOrder: MediaSortOrder
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumContextMenuStateProvider(savedStateHandle: SavedStateHandle): AlbumContextMenuState {
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val selectedSort = savedStateHandle.get<String>(NavArgs.SORT_OPTION)!!
        val sortOrder = savedStateHandle.get<String>(NavArgs.SORT_ORDER)!!
        return AlbumContextMenuState(
            mediaId = mediaId,
            menuTitle = "",
            listItems = listOf(),
            selectedSort = selectedSort,
            sortOrder = MediaSortOrder.valueOf(sortOrder)
        )
    }
}


