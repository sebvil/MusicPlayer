package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolderFactory

class SortMenuStateHolderFactory(private val dependencyContainer: DependencyContainer) :
    StateHolderFactory<SortMenuArguments, SortMenuStateHolder> {
    override fun getStateHolder(arguments: SortMenuArguments): SortMenuStateHolder {
        return SortMenuStateHolder(
            arguments,
            sortPreferencesRepository = dependencyContainer.repositoryProvider.sortPreferencesRepository
        )
    }
}