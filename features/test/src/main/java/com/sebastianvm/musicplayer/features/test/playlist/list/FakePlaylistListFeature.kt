package com.sebastianvm.musicplayer.features.test.playlist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature

class FakePlaylistListFeature : PlaylistListFeature {
    override fun playlistListUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "PlaylistList")
    }
}
