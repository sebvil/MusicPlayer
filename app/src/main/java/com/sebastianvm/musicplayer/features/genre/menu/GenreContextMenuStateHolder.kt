package com.sebastianvm.musicplayer.features.genre.menu

import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
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

data class GenreContextMenuArguments(val genreId: Long) : Arguments

sealed interface GenreContextMenuState : State {
    data class Data(val genreName: String, val genreId: Long) : GenreContextMenuState

    data object Loading : GenreContextMenuState
}

sealed interface GenreContextMenuUserAction : UserAction {
    data object PlayGenreClicked : GenreContextMenuUserAction
}

class GenreContextMenuStateHolder(
    arguments: GenreContextMenuArguments,
    genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    recompositionMode: RecompositionMode = RecompositionMode.ContextClock,
) : StateHolder<GenreContextMenuState, GenreContextMenuUserAction> {

    private val genreId = arguments.genreId

    override val state: StateFlow<GenreContextMenuState> =
        stateHolderScope.launchMolecule(recompositionMode) {
            val genreName = genreRepository.getGenreName(genreId).collectValue(initial = null)
            if (genreName == null) {
                GenreContextMenuState.Loading
            } else {
                GenreContextMenuState.Data(genreName = genreName, genreId = genreId)
            }
        }

    override fun handle(action: GenreContextMenuUserAction) {
        when (action) {
            GenreContextMenuUserAction.PlayGenreClicked -> {
                stateHolderScope.launch {
                    playbackManager.playMedia(mediaGroup = MediaGroup.Genre(genreId))
                }
            }
        }
    }
}
