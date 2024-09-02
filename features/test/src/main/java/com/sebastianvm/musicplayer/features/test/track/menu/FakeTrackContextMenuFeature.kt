package com.sebastianvm.musicplayer.features.test.track.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature

class FakeTrackContextMenuFeature : TrackContextMenuFeature {
    override fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
    ): MvvmComponent {
        return FakeMvvmComponent(arguments, "TrackContextMenu")
    }
}
