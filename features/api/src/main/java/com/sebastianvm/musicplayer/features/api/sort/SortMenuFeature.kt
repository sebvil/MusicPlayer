package com.sebastianvm.musicplayer.features.api.sort

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent

interface SortMenuFeature {
    fun sortMenuUiComponent(
        arguments: SortMenuArguments,
    ): UiComponent<*>
}
