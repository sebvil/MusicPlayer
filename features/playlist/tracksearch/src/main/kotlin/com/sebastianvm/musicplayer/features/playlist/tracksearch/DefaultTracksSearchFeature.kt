package com.sebastianvm.musicplayer.features.playlist.tracksearch

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchArguments
import com.sebastianvm.musicplayer.features.api.playlist.tracksearch.TrackSearchFeature

class DefaultTracksSearchFeature : TrackSearchFeature {
    override fun trackSearchUiComponent(
        arguments: TrackSearchArguments,
        navController: NavController,
    ): UiComponent<*> {
        return TrackSearchUiComponent(arguments, navController)
    }
}
