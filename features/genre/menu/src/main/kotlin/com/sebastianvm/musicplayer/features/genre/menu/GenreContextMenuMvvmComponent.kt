package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.annotations.MvvmComponent
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler

@MvvmComponent(vmClass = GenreContextMenuViewModel::class)
@Composable
fun GenreContextMenu(
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
