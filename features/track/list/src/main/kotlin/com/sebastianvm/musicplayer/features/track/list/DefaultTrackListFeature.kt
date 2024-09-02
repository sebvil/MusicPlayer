package com.sebastianvm.musicplayer.features.track.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.track.list.TrackListFeature

class DefaultTrackListFeature : TrackListFeature {
    override fun trackListUiComponent(navController: NavController): MvvmComponent {
        return TrackListMvvmComponent(navController)
    }
}
