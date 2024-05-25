package com.sebastianvm.musicplayer.features.artist.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun artistContextMenuStateHolderFactory(): StateHolderFactory<ArtistContextMenuArguments, ArtistContextMenuDelegate, ArtistContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args, delegate ->
        ArtistContextMenuStateHolder(
            arguments = args,
            artistRepository = dependencyContainer.repositoryProvider.artistRepository,
            delegate = delegate
        )
    }
}