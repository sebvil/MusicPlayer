package com.sebastianvm.musicplayer.features.track.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuArguments
import com.sebastianvm.musicplayer.features.api.track.menu.TrackContextMenuFeature

class DefaultTrackContextMenuFeature : TrackContextMenuFeature {
    override fun trackContextMenuUiComponent(
        arguments: TrackContextMenuArguments,
        navController: NavController,
    ): MvvmComponent {
        return TrackContextMenuMvvmComponent(arguments, navController)
    }
}
