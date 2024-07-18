package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.core.designsystems.components.MenuItem
import com.sebastianvm.musicplayer.core.designsystems.icons.AppIcons
import com.sebastianvm.musicplayer.core.resources.RString
import com.sebastianvm.musicplayer.core.ui.components.ContextMenu
import com.sebastianvm.musicplayer.core.ui.mvvm.Handler
import com.sebastianvm.musicplayer.di.Dependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent

data class ArtistContextMenu(override val arguments: ArtistContextMenuArguments) :
    BaseUiComponent<
        ArtistContextMenuArguments,
        ArtistContextMenuState,
        ArtistContextMenuUserAction,
        ArtistContextMenuStateHolder,
    >() {

    override fun createStateHolder(dependencies: Dependencies): ArtistContextMenuStateHolder {
        return getArtistContextMenuStateHolder(dependencies, arguments)
    }

    @Composable
    override fun Content(
        state: ArtistContextMenuState,
        handle: Handler<ArtistContextMenuUserAction>,
        modifier: Modifier,
    ) {
        ArtistContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun ArtistContextMenu(
    state: ArtistContextMenuState,
    handle: Handler<ArtistContextMenuUserAction>,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is ArtistContextMenuState.Data -> {
            ContextMenu(menuTitle = state.artistName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = RString.play_all_songs),
                            icon = AppIcons.PlayArrow.icon(),
                            onItemClick = { handle(ArtistContextMenuUserAction.PlayArtistClicked) },
                        )
                    }
                }
            }
        }
        is ArtistContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = RString.loading), modifier = modifier) {}
        }
    }
}
