package com.sebastianvm.musicplayer.features.test.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature

class FakeTrackSearchFeature : TrackSearchFeature {
    override fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): UiComponent<*> {
        return FakeUiComponent(arguments, "TrackSearch")
    }
}
