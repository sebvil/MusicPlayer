package com.sebastianvm.musicplayer.core.servicestest.features.track.menu

import com.sebastianvm.musicplayer.core.servicestest.features.navigation.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.menu.TrackContextMenuFeature
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

class FakeTrackContextMenuFeature :
    com.sebastianvm.musicplayer.features.api.menu.TrackContextMenuFeature {
    override fun trackContextMenuUiComponent(
        arguments: com.sebastianvm.musicplayer.features.api.menu.TrackContextMenuArguments,
        navController: NavController,
    ): UiComponent<*> {
        return FakeUiComponent(arguments)
    }
}
