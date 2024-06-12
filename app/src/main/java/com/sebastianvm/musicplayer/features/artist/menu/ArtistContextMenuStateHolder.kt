package com.sebastianvm.musicplayer.features.artist.menu

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.extensions.collectValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
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
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<ArtistContextMenuState, ArtistContextMenuUserAction> {

    private val artistId = arguments.artistId

    override val state: StateFlow<ArtistContextMenuState> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val artist = artistRepository.getArtist(artistId).collectValue(initial = null)
            if (artist == null) {
                ArtistContextMenuState.Loading
            } else {
                ArtistContextMenuState.Data(artistName = artist.name, artistId = artistId)
            }
        }

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
    dependencies: Dependencies,
    arguments: ArtistContextMenuArguments,
): ArtistContextMenuStateHolder {
    return ArtistContextMenuStateHolder(
        arguments = arguments,
        artistRepository = dependencies.repositoryProvider.artistRepository,
        playbackManager = dependencies.repositoryProvider.playbackManager,
    )
}
