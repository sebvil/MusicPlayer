package com.sebastianvm.musicplayer.features.playlist.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature

class DefaultPlaylistContextMenuFeature : PlaylistContextMenuFeature {
    override fun playlistContextMenuUiComponent(
        arguments: PlaylistContextMenuArguments,
        delegate: PlaylistContextMenuDelegate
    ): UiComponent<*> {
        return PlaylistContextMenuUiComponent(arguments, delegate)
    }
}
