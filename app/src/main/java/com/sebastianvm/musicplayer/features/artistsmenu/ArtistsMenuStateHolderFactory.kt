package com.sebastianvm.musicplayer.features.artistsmenu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun artistsMenuStateHolderFactory(): StateHolderFactory<ArtistsMenuArguments, ArtistsMenuDelegate, ArtistsMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args, delegate ->
        ArtistsMenuStateHolder(
            arguments = args,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            delegate = delegate
        )
    }
}