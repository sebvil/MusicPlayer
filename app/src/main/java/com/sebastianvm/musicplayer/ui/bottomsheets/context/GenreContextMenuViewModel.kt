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
class GenreContextMenuViewModel @Inject constructor(
    initialState: GenreContextMenuState,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<GenreContextMenuState>(initialState) {

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                launchViewModelIOScope {
                    val mediaGroup = MediaGroup(MediaGroupType.GENRE, state.value.genreName)
                    mediaQueueRepository.createQueue(
                        mediaGroup = mediaGroup,
                        sortOrder = state.value.sortOrder,
                        sortOption = state.value.selectedSort
                    )
                    mediaPlaybackRepository.playFromId(state.value.genreName, mediaGroup)
                    addUiEvent(BaseContextMenuUiEvent.NavigateToPlayer)
                }
            }
            is ContextMenuItem.ViewGenre -> {
                addUiEvent(BaseContextMenuUiEvent.NavigateToGenre(genreName = state.value.genreName))
            }
            else -> throw IllegalStateException("Invalid row for genre context menu")
        }
    }
}

data class GenreContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val genreName: String,
    val mediaGroup: MediaGroup,
    val selectedSort: SortOption,
    val sortOrder: SortOrder
) : BaseContextMenuState(listItems = listItems, menuTitle = menuTitle)

@InstallIn(ViewModelComponent::class)
@Module
object InitialGenreContextMenuStateModule {
    @Provides
    @ViewModelScoped
    fun initialGenreContextMenuStateProvider(savedStateHandle: SavedStateHandle): GenreContextMenuState {
        val genreName = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        val mediaGroupType =
            MediaGroupType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_TYPE)!!)
        val mediaGroupMediaId = savedStateHandle.get<String>(NavArgs.MEDIA_GROUP_ID)!!
        val selectedSort = savedStateHandle.get<String>(NavArgs.SORT_OPTION)!!
        val sortOrder = savedStateHandle.get<String>(NavArgs.SORT_ORDER)!!
        return GenreContextMenuState(
            genreName = genreName,
            menuTitle = genreName,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            ),
            selectedSort = SortOption.valueOf(selectedSort),
            sortOrder = SortOrder.valueOf(sortOrder)
        )
    }
}


