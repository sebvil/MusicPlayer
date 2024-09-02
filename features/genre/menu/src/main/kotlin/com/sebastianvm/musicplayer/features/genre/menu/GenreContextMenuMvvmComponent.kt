package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseMvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments

data class GenreContextMenuMvvmComponent(
    val arguments: GenreContextMenuArguments,
    private val genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager
) :
    BaseMvvmComponent<GenreContextMenuState, GenreContextMenuUserAction, GenreContextMenuViewModel>() {

    override val viewModel: GenreContextMenuViewModel by lazy {
        GenreContextMenuViewModel(
            arguments = arguments,
            genreRepository = genreRepository,
            playbackManager = playbackManager,
        )
    }

    @Composable
    override fun Content(
        state: GenreContextMenuState,
        handle: Handler<GenreContextMenuUserAction>,
        modifier: Modifier,
    ) {
        GenreContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun GenreContextMenu(
    state: GenreContextMenuState,
    handle: Handler<GenreContextMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is GenreContextMenuState.Data -> {
            ContextMenu(menuTitle = state.genreName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = RString.play_all_songs),
                            icon = AppIcons.PlayArrow.icon(),
                            onItemClick = { handle(GenreContextMenuUserAction.PlayGenreClicked) },
                        )
                    }
                }
            }
        }

        is GenreContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = RString.loading), modifier = modifier) {}
        }
    }
}
