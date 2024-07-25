package com.sebastianvm.musicplayer.features.api.track.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.Features

interface TrackContextMenuFeature {
    fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
        features: Features,
    ): UiComponent<*>
}
