package com.sebastianvm.musicplayer.features.playlist.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun playlistContextMenuStateHolderFactory(): StateHolderFactory<PlaylistContextMenuArguments, PlaylistContextMenuDelegate, PlaylistContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args, delegate ->
        PlaylistContextMenuStateHolder(
            arguments = args,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            delegate = delegate
        )
    }
}
