package com.sebastianvm.musicplayer.features.track.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun trackContextMenuStateHolderFactory(): StateHolderFactory<TrackContextMenuArguments, TrackContextMenuDelegate, TrackContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, arguments, delegate ->
        TrackContextMenuStateHolder(
            arguments = arguments,
            trackRepository = dependencyContainer.repositoryProvider.trackRepository,
            playlistRepository = dependencyContainer.repositoryProvider.playlistRepository,
            playbackManager = dependencyContainer.repositoryProvider.playbackManager,
            delegate = delegate
        )
    }
}
