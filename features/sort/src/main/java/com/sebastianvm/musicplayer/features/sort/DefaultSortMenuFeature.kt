package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortMenuFeature

class DefaultSortMenuFeature : SortMenuFeature {
    override fun sortMenuUiComponent(arguments: SortMenuArguments): UiComponent<*> {
        return SortMenuUiComponent(arguments)
    }
}
