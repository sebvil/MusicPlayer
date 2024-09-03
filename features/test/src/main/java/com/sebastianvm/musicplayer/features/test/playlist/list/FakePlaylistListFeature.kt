package com.sebastianvm.musicplayer.features.test.playlist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListFeature

class FakePlaylistListFeature : PlaylistListFeature {
    override fun playlistListUiComponent(navController: NavController): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments = null, name = "PlaylistList")
    }
}
