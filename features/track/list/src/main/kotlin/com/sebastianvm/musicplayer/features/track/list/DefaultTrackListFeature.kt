package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature

class DefaultTrackListFeature : TrackListFeature {
    override fun trackListUiComponent(navController: NavController): UiComponent<*> {
        return TrackListUiComponent(navController)
    }
}
