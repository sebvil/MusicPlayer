package com.sebastianvm.musicplayer.features.playlist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature

class DefaultPlaylistListFeature : PlaylistListFeature {
    override fun playlistListUiComponent(navController: NavController): UiComponent<*> {
        return PlaylistListUiComponent(navController)
    }
}
