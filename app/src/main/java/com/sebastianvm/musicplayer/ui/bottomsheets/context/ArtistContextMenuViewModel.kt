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
class ArtistContextMenuViewModel @Inject constructor(
    initialState: ArtistContextMenuState,
    private val mediaQueueRepository: MediaQueueRepository,
    private val mediaPlaybackRepository: MediaPlaybackRepository,
) : BaseContextMenuViewModel<ArtistContextMenuState>(initialState) {

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                viewModelScope.launch {
                    val mediaGroup = MediaGroup(MediaGroupType.ARTIST, state.value.artistName)
                    mediaQueueRepository.createQueue(mediaGroup = mediaGroup)
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

data class ArtistContextMenuState(
    override val listItems: List<ContextMenuItem>,
    override val menuTitle: String,
    val artistName: String,
    override val events: List<BaseContextMenuUiEvent>
) : BaseContextMenuState(listItems, menuTitle) {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<BaseContextMenuUiEvent>> setEvent(events: List<BaseContextMenuUiEvent>): S {
        return copy(events = events) as S
    }
}

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
            events = listOf()
        )
    }
}


