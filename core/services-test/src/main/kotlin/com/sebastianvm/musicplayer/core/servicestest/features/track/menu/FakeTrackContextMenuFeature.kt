package com.sebastianvm.musicplayer.core.servicestest.features.track.menu

import com.sebastianvm.musicplayer.core.servicestest.features.navigation.FakeUiComponent
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.services.features.track.menu.TrackContextMenuFeature

class FakeTrackContextMenuFeature : TrackContextMenuFeature {
    override fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController
    ): UiComponent<TrackContextMenuArguments, *> {
        return FakeUiComponent(arguments)
    }
}
