package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.designsystem.icons.PlayArrow
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.navigation.BaseUiComponent
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler

data class ArtistContextMenu(
    override val arguments: ArtistContextMenuArguments,
) : BaseUiComponent<ArtistContextMenuArguments, ArtistContextMenuState, ArtistContextMenuUserAction, ArtistContextMenuStateHolder>() {

    override fun createStateHolder(dependencies: AppDependencies): ArtistContextMenuStateHolder {
        return getArtistContextMenuStateHolder(dependencies, arguments)
    }

    @Composable
    override fun Content(
        state: ArtistContextMenuState,
        handle: Handler<ArtistContextMenuUserAction>,
        modifier: Modifier
    ) {
        ArtistContextMenu(state = state, handle = handle, modifier = modifier)
    }
}

@Composable
private fun ArtistContextMenu(
    state: ArtistContextMenuState,
    handle: Handler<ArtistContextMenuUserAction>,
    modifier: Modifier = Modifier
) {
    when (state) {
        is ArtistContextMenuState.Data -> {
            ContextMenu(menuTitle = state.artistName, modifier = modifier) {
                LazyColumn {
                    item {
                        MenuItem(
                            text = stringResource(id = R.string.play_all_songs),
                            icon = Icons.PlayArrow.icon(),
                            onItemClicked = {
                                handle(ArtistContextMenuUserAction.PlayArtistClicked)
                            }
                        )
                    }
                }
            }
        }

        is ArtistContextMenuState.Loading -> {
            ContextMenu(menuTitle = stringResource(id = R.string.loading), modifier = modifier) {}
        }
    }
}
