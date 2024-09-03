package com.sebastianvm.musicplayer.features.test.playlist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsArguments
import com.sebastianvm.musicplayer.features.api.playlist.details.PlaylistDetailsFeature

class FakePlaylistDetailsFeature : PlaylistDetailsFeature {
    override fun playlistDetailsUiComponent(
        arguments: PlaylistDetailsArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments, "PlaylistDetails")
    }
}
