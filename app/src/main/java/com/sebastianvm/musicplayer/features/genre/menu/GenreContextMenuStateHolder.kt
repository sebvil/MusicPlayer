package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.track.list.TrackList
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.genre.GenreRepository
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

data class GenreContextMenuArguments(val genreId: Long) : Arguments
interface GenreContextMenuDelegate : NavController

sealed interface GenreContextMenuState : State {
    data class Data(
        val genreName: String,
        val genreId: Long,
    ) : GenreContextMenuState

    data object Loading : GenreContextMenuState
}

sealed interface GenreContextMenuUserAction : UserAction {
    data object PlayGenreClicked : GenreContextMenuUserAction
    data object ViewGenreClicked : GenreContextMenuUserAction
}

class GenreContextMenuStateHolder(
    arguments: GenreContextMenuArguments,
    genreRepository: GenreRepository,
    private val delegate: GenreContextMenuDelegate,
    stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<GenreContextMenuState, GenreContextMenuUserAction> {

    private val genreId = arguments.genreId

    override val state: StateFlow<GenreContextMenuState> =
        genreRepository.getGenreName(genreId).map { genreName ->
            GenreContextMenuState.Data(
                genreName = genreName,
                genreId = genreId,
            )
        }.stateIn(stateHolderScope, SharingStarted.Lazily, GenreContextMenuState.Loading)

    override fun handle(action: GenreContextMenuUserAction) {
        when (action) {
            GenreContextMenuUserAction.PlayGenreClicked -> {
            }

            GenreContextMenuUserAction.ViewGenreClicked -> {
                delegate.push(
                    TrackList(
                        arguments = TrackListArguments(MediaGroup.Genre(genreId)),
                        navController = delegate
                    )
                )
            }
        }
    }
}
