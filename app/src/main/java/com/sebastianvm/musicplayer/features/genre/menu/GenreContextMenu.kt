package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.icons.Genre
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.designsystem.icons.PlayArrow
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseScreen
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

data class GenreContextMenu(
    override val arguments: GenreContextMenuArguments,
    val navController: NavController
) : BaseScreen<GenreContextMenuArguments, GenreContextMenuStateHolder>() {

    override fun createStateHolder(dependencies: DependencyContainer): GenreContextMenuStateHolder {
        return getGenreContextMenuStateHolder(dependencies, arguments, navController)
    }

    @Composable
    override fun Content(stateHolder: GenreContextMenuStateHolder, modifier: Modifier) {
        GenreContextMenu(
            stateHolder = stateHolder,
            modifier = modifier
        )
    }
}

@Composable
fun GenreContextMenu(
    stateHolder: GenreContextMenuStateHolder,
    modifier: Modifier = Modifier
) {
    val state by stateHolder.currentState
    GenreContextMenu(state = state, handle = stateHolder::handle, modifier = modifier)
}

@Composable
private fun GenreContextMenu(
    state: GenreContextMenuState,
    handle: Handler<GenreContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    when (state) {
        is GenreContextMenuState.Data -> {
            ContextMenu(menuTitle = state.genreName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = R.string.play_all_songs),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = {
                                handle(GenreContextMenuUserAction.PlayGenreClicked)
                            }
                        )
                    }

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.view_genre),
                            icon = Icons.Genre.icon(),
                            onItemClicked = {
                                handle(GenreContextMenuUserAction.ViewGenreClicked)
                            }
                        )
                    }
                }
            }
        }

        is GenreContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}
