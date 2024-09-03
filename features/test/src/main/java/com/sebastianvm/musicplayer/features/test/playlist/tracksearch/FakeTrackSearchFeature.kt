package com.sebastianvm.musicplayer.features.test.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature

class FakeTrackSearchFeature : TrackSearchFeature {
    override fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments, "TrackSearch")
    }
}
