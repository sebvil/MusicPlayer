package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
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
class ArtistContextMenuViewModel @Inject constructor(
    initialState: ArtistContextMenuState,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<ArtistContextMenuState>(initialState) {

    override fun handle(action: BaseContextMenuUserAction) {
        when (action) {
            is BaseContextMenuUserAction.RowClicked -> {
                when (action.row) {
                    is ContextMenuItem.PlayAllSongs -> {
                        launchViewModelIOScope {
                            val mediaGroup = MediaGroup(MediaGroupType.ARTIST, state.value.artistName)
                            mediaQueueRepository.createQueue(
                                mediaGroup = mediaGroup,
                                sortOrder = SortOrder.ASCENDING,
                                sortOption = SortOption.TRACK_NAME
                            )
                            mediaPlaybackRepository.playFromId(state.value.artistName, mediaGroup)
                            addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                        }
                    }
                    is ContextMenuItem.ViewArtist -> {
                        addUiEvent(BaseContextMenuUiEvent.NavigateToArtist(state.value.artistName))
                    }
                    else -> throw IllegalStateException("Invalid row for artist context menu")
                }
            }
        }
    }
}

data class ArtistContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val artistName: String,
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistContextMenuStateProvider(savedStateHandle: SavedStateHandle): ArtistContextMenuState {
        val artistName = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        return ArtistContextMenuState(
            artistName = artistName,
            menuTitle = artistName,
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewArtist
            ),
        )
    }
}


