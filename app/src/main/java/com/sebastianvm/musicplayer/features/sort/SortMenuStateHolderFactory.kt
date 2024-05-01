package com.sebastianvm.musicplayer.features.sort

import androidx.compose.runtime.Composable
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory
import com.sebastianvm.musicplayer.ui.util.mvvm.stateHolderFactory


@Composable
fun sortMenuStateHolderFactory(): StateHolderFactory<SortMenuArguments, SortMenuStateHolder> {
    return stateHolderFactory { dependencyContainer, args ->
        SortMenuStateHolder(
            arguments = args,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository,
        )
    }
}