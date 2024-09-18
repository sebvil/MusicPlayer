package com.sebastianvm.musicplayer.features.artist.menu

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.playback.manager.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ArtistContextMenuState : State {
    data class Data(val artistName: String, val artistId: Long) : ArtistContextMenuState

    data object Loading : ArtistContextMenuState
}

sealed interface ArtistContextMenuUserAction : UserAction {
    data object PlayArtistClicked : ArtistContextMenuUserAction
}

class ArtistContextMenuViewModel(
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
    private val playbackManager: PlaybackManager,
    viewModelScope: CoroutineScope = getViewModelScope(),
) :
    BaseViewModel<ArtistContextMenuState, ArtistContextMenuUserAction>(
        viewModelScope = viewModelScope
    ) {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistContextMenuState> =
        artistRepository
            .getArtist(artistId)
            .map { artist ->
                ArtistContextMenuState.Data(artistName = artist.name, artistId = artistId)
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, ArtistContextMenuState.Loading)

    override fun handle(action: ArtistContextMenuUserAction) {
        when (action) {
            ArtistContextMenuUserAction.PlayArtistClicked -> {
                viewModelScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Artist(artistId))
                }
            }
        }
    }
}
