package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.core.data.di.RepositoryProvider
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature

class DefaultGenreContextMenuFeature(
    private val repositoryProvider: RepositoryProvider,
    private val playbackManager: PlaybackManager,
) : GenreContextMenuFeature {
    override fun genreContextMenuUiComponent(
        arguments: GenreContextMenuArguments
    ): MvvmComponent<*, *, *> {
        return GenreContextMenuMvvmComponent(
            arguments = arguments,
            genreRepository = repositoryProvider.genreRepository,
            playbackManager = playbackManager,
        )
    }
}
