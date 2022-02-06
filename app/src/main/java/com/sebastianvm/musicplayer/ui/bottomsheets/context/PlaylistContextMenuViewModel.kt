package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.playlist.PlaylistRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.launchViewModelIOScope
import com.sebastianvm.musicplayer.util.SortOption
import com.sebastianvm.musicplayer.util.SortOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@HiltViewModel
class PlaylistContextMenuViewModel @Inject constructor(
    initialState: PlaylistContextMenuState,
    private val playlistRepository: PlaylistRepository,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<PlaylistContextMenuState>(initialState) {

    // TODO check if playlist has items before playing
    override fun handle(action: BaseContextMenuUserAction) {
        when (action) {
            is BaseContextMenuUserAction.RowClicked -> {
                when (action.row) {
                    is ContextMenuItem.PlayAllSongs -> {
                        launchViewModelIOScope {
                            val mediaGroup =
                                MediaGroup(MediaGroupType.PLAYLIST, state.value.playlistName)
                            mediaQueueRepository.createQueue(
                                mediaGroup = mediaGroup,
                                sortOrder = state.value.sortOrder,
                                sortOption = state.value.selectedSort
                            )
                            mediaPlaybackRepository.playFromId(state.value.playlistName, mediaGroup)
                            addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                        }
                    }
                    is ContextMenuItem.ViewPlaylist -> {
                        addUiEvent(BaseContextMenuUiEvent.NavigateToPlaylist(playlistName = state.value.playlistName))
                    }
                    is ContextMenuItem.DeletePlaylist -> {
                        launchViewModelIOScope {
                            playlistRepository.deletePlaylist(state.value.playlistName)
                        }
                    }
                    else -> throw IllegalStateException("Invalid row for playlist context menu")
                }
            }
        }
    }
}

data class PlaylistContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val playlistName: String,
    val mediaGroup: MediaGroup,
    val selectedSort: SortOption,
    val sortOrder: SortOrder
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialPlaylistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialPlaylistContextMenuStateProvider(savedStateHandle: SavedStateHandle): PlaylistContextMenuState {
        val playlistName = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        val selectedSort = savedStateHandle.get<String>(NavArgs.SORT_OPTION)!!
        val sortOrder = savedStateHandle.get<String>(NavArgs.SORT_ORDER)!!
        return PlaylistContextMenuState(
            playlistName = playlistName,
            menuTitle = playlistName,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewPlaylist,
                ContextMenuItem.DeletePlaylist
            ),
            selectedSort = SortOption.valueOf(selectedSort),
            sortOrder = SortOrder.valueOf(sortOrder)
        )
    }
}


