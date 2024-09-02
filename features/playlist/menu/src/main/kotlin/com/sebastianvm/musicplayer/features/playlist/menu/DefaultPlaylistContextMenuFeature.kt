package com.sebastianvm.musicplayer.features.playlist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature

class DefaultPlaylistContextMenuFeature : PlaylistContextMenuFeature {
    override fun playlistContextMenuUiComponent(
        arguments: PlaylistContextMenuArguments,
        delegate: PlaylistContextMenuDelegate,
    ): MvvmComponent {
        return PlaylistContextMenuMvvmComponent(arguments, delegate)
    }
}
