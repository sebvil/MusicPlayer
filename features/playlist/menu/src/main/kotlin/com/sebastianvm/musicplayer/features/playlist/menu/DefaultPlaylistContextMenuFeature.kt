package com.sebastianvm.musicplayer.features.playlist.menu

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuDelegate
import com.sebastianvm.musicplayer.features.api.playlist.menu.PlaylistContextMenuFeature

class DefaultPlaylistContextMenuFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
) : PlaylistContextMenuFeature {
    override fun playlistContextMenuUiComponent(
        arguments: PlaylistContextMenuArguments,
        delegate: PlaylistContextMenuDelegate,
    ): MvvmComponent {
        return PlaylistContextMenuMvvmComponent(
            arguments = arguments,
            delegate = delegate,
            playlistRepository = repositoryProvider.playlistRepository,
            playbackManager = playbackManager,
        )
    }
}
