package com.sebastianvm.musicplayer.features.test.genre.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature

class FakeGenreContextMenuFeature : GenreContextMenuFeature {
    override fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): UiComponent<*> {
        return FakeUiComponent(arguments, "GenreContextMenu")
    }
}
