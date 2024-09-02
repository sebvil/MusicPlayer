package com.sebastianvm.musicplayer.features.test.track.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature

class FakeTrackListFeature : TrackListFeature {
    override fun trackListUiComponent(navController: NavController): MvvmComponent {
        return FakeMvvmComponent(arguments = null, name = "TrackList")
    }
}
