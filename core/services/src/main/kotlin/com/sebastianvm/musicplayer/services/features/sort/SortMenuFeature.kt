package com.sebastianvm.musicplayer.services.features.sort

import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

interface SortMenuFeature {
    fun sortMenuUiComponent(
        arguments: SortMenuArguments,
    ): UiComponent<SortMenuArguments, *>
}
