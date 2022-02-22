package com.sebastianvm.musicplayer.ui.bottomsheets.context


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaGroupType
import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.repository.queue.MediaQueueRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
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
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(MediaGroupType.GENRE, state.value.genreName)
                    mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
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
    override val events: BaseContextMenuUiEvent?
) : BaseContextMenuState(listItems, menuTitle) {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<BaseContextMenuUiEvent>> setEvent(event: BaseContextMenuUiEvent?): S {
        return copy(events = event) as S
    }
}

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
        return GenreContextMenuState(
            genreName = genreName,
            menuTitle = genreName,
            mediaGroup = MediaGroup(mediaGroupType, mediaGroupMediaId),
            listItems = listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            ),
            events = null
        )
    }
}


