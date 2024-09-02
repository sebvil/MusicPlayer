package com.sebastianvm.musicplayer.features.test.playlist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature

class FakePlaylistContextMenuFeature : PlaylistContextMenuFeature {
    override fun playlistContextMenuUiComponent(
        arguments: PlaylistContextMenuArguments,
        delegate: PlaylistContextMenuDelegate,
    ): MvvmComponent {
        return FakeMvvmComponent(arguments, "PlaylistContextMenu")
    }
}
