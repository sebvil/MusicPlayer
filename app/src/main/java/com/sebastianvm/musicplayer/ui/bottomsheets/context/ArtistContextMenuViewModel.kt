package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
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
class ArtistContextMenuViewModel @Inject constructor(
    initialState: ArtistContextMenuState,
    private val playbackManager: PlaybackManager,
) : BaseContextMenuViewModel<ArtistContextMenuState>(initialState) {

    override fun onRowClicked(row: ContextMenuItem) {
        when (row) {
            is ContextMenuItem.PlayAllSongs -> {
                viewModelScope.launch {
                    playbackManager.playArtist(state.value.artistName)
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
) : BaseContextMenuState(listItems, menuTitle)


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


