package com.sebastianvm.musicplayer.features.genre.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature

class DefaultGenreContextMenuFeature : GenreContextMenuFeature {
    override fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): UiComponent<*> {
        return GenreContextMenuUiComponent(arguments)
    }
}
