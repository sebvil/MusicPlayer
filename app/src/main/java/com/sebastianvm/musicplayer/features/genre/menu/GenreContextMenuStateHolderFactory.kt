package com.sebastianvm.musicplayer.features.genre.menu

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory

@Composable
fun genreContextMenuStateHolderFactory(): StateHolderFactory<GenreContextMenuArguments, GenreContextMenuDelegate, GenreContextMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args, delegate ->
        GenreContextMenuStateHolder(
            arguments = args,
            genreRepository = dependencyContainer.repositoryProvider.genreRepository,
            delegate = delegate,
        )
    }
}
