package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.navigation.BaseScreen
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.ui.ContextMenu
import com.sebastianvm.musicplayer.ui.MenuItem
import com.sebastianvm.musicplayer.ui.icons.Artist
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.icons.PlayArrow
import com.sebastianvm.musicplayer.ui.util.mvvm.Handler
import com.sebastianvm.musicplayer.ui.util.mvvm.currentState

data class ArtistContextMenu(
    override val arguments: ArtistContextMenuArguments,
    val navController: NavController
) : BaseScreen<ArtistContextMenuArguments, ArtistContextMenuStateHolder>() {

    override fun createStateHolder(dependencies: DependencyContainer): ArtistContextMenuStateHolder {
        return getArtistContextMenuStateHolder(dependencies, arguments, navController)
    }

    @Composable
    override fun Content(stateHolder: ArtistContextMenuStateHolder, modifier: Modifier) {
        ArtistContextMenu(
            stateHolder = stateHolder,
            modifier = modifier,
        )
    }
}

@Composable
fun ArtistContextMenu(
    stateHolder: ArtistContextMenuStateHolder,
    modifier: Modifier = Modifier
) {
    val state by stateHolder.currentState
    ArtistContextMenu(state = state, handle = stateHolder::handle, modifier = modifier)
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

                    item {
                        MenuItem(
                            text = stringResource(id = R.string.view_artist),
                            icon = Icons.Artist.icon(),
                            onItemClicked = {
                                handle(ArtistContextMenuUserAction.ViewArtistClicked)
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
