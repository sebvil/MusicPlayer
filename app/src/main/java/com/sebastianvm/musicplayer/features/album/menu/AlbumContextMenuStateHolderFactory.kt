package com.sebastianvm.musicplayer.features.album.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun albumContextMenuStateHolderFactory(): StateHolderFactory<AlbumContextMenuArguments, AlbumContextMenuDelegate, AlbumContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args, delegate ->

        AlbumContextMenuStateHolder(
            arguments = args,
            albumRepository = dependencyContainer.repositoryProvider.albumRepository,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = delegate
        )
    }
}
