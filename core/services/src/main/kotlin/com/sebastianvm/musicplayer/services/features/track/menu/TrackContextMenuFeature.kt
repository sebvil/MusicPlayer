package com.sebastianvm.musicplayer.services.features.track.menu

import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

interface TrackContextMenuFeature {
    fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController
    ): UiComponent<TrackContextMenuArguments, *>
}
