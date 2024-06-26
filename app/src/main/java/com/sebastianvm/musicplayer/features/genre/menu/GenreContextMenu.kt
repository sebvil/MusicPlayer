package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.designsystem.icons.AppIcons
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

data class GenreContextMenu(override val arguments: GenreContextMenuArguments) :
    BaseUiComponent<
        GenreContextMenuArguments,
        GenreContextMenuState,
        GenreContextMenuUserAction,
        GenreContextMenuStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): GenreContextMenuStateHolder {
        return GenreContextMenuStateHolder(
            arguments = arguments,
            genreRepository = dependencies.repositoryProvider.genreRepository,
            playbackManager = dependencies.playbackManager,
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
