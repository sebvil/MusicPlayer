package com.sebastianvm.musicplayer.features.artist.menu

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature

class DefaultArtistContextMenuFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
) : ArtistContextMenuFeature {
    override fun artistContextMenuUiComponent(
        arguments: ArtistContextMenuArguments
    ): MvvmComponent<*, *, *> {
        return ArtistContextMenuMvvmComponent(
            arguments = arguments,
            artistRepository = repositoryProvider.artistRepository,
            playbackManager = playbackManager,
        )
    }
}
