package com.sebastianvm.musicplayer.features.artist.menu

import com.sebastianvm.musicplayer.core.data.artist.ArtistRepository
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.services.Services
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ArtistContextMenuArguments(val artistId: Long) : Arguments

sealed interface ArtistContextMenuState : State {
    data class Data(val artistName: String, val artistId: Long) : ArtistContextMenuState

    data object Loading : ArtistContextMenuState
}

sealed interface ArtistContextMenuUserAction : UserAction {
    data object PlayArtistClicked : ArtistContextMenuUserAction
}

class ArtistContextMenuStateHolder(
    arguments: ArtistContextMenuArguments,
    artistRepository: ArtistRepository,
    private val playbackManager: PlaybackManager,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<ArtistContextMenuState, ArtistContextMenuUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistContextMenuState> =
        artistRepository
            .getArtist(artistId)
            .map { artist ->
                ArtistContextMenuState.Data(artistName = artist.name, artistId = artistId)
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, ArtistContextMenuState.Loading)

    override fun handle(action: ArtistContextMenuUserAction) {
        when (action) {
            ArtistContextMenuUserAction.PlayArtistClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Artist(artistId))
                }
            }
        }
    }
}

fun getArtistContextMenuStateHolder(
    services: Services,
    arguments: ArtistContextMenuArguments,
): ArtistContextMenuStateHolder {
    return ArtistContextMenuStateHolder(
        arguments = arguments,
        artistRepository = services.repositoryProvider.artistRepository,
        playbackManager = services.playbackManager,
    )
}
