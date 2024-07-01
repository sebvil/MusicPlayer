package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.data.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
) : StateHolder<GenreContextMenuState, GenreContextMenuUserAction> {

    private val genreId = arguments.genreId

    override val state: StateFlow<GenreContextMenuState> =
        genreRepository
            .getGenreName(genreId)
            .map { genreName ->
                GenreContextMenuState.Data(genreName = genreName, genreId = genreId)
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, GenreContextMenuState.Loading)

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
