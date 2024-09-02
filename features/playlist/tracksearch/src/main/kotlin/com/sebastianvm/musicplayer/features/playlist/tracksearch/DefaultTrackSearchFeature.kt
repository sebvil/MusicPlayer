package com.sebastianvm.musicplayer.features.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature

class DefaultTrackSearchFeature : TrackSearchFeature {
    override fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): MvvmComponent {
        return TrackSearchMvvmComponent(arguments, navController)
    }
}
