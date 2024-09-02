package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature

class DefaultSortMenuFeature : SortMenuFeature {
    override fun sortMenuUiComponent(arguments: SortMenuArguments): MvvmComponent {
        return SortMenuMvvmComponent(arguments)
    }
}
