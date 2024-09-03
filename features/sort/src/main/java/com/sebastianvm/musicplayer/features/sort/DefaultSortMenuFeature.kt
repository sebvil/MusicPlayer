package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature

class DefaultSortMenuFeature(private val repositoryProvider: RepositoryProvider) : SortMenuFeature {
    override fun sortMenuUiComponent(arguments: SortMenuArguments): MvvmComponent<*, *, *> {
        return SortMenuMvvmComponent(
            arguments = arguments,
            sortPreferencesRepository = repositoryProvider.sortPreferencesRepository,
        )
    }
}
