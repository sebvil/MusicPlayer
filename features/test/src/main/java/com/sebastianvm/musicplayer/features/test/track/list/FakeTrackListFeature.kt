package com.sebastianvm.musicplayer.features.test.track.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature

class FakeTrackListFeature : TrackListFeature {
    override fun trackListUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "TrackList")
    }
}
