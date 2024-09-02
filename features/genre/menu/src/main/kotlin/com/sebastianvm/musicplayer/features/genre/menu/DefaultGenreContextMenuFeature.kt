package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.core.data.genre.GenreRepository
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature

class DefaultGenreContextMenuFeature(
    private val genreRepository: GenreRepository,
    private val playbackManager: PlaybackManager
) : GenreContextMenuFeature {
    override fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): MvvmComponent {
        return GenreContextMenuMvvmComponent(arguments, genreRepository, playbackManager)
    }
}
