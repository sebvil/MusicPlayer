package com.sebastianvm.musicplayer.features.playlist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature

class DefaultPlaylistDetailsFeature : PlaylistDetailsFeature {
    override fun playlistDetailsUiComponent(
        arguments: PlaylistDetailsArguments,
        navController: NavController
    ): UiComponent<*> {
        return PlaylistDetailsUiComponent(arguments, navController)
    }
}
