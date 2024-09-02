package com.sebastianvm.musicplayer.features.test.genre.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuArguments
import com.sebastianvm.musicplayer.features.api.genre.menu.GenreContextMenuFeature

class FakeGenreContextMenuFeature : GenreContextMenuFeature {
    override fun genreContextMenuUiComponent(arguments: GenreContextMenuArguments): MvvmComponent {
        return FakeMvvmComponent(arguments, "GenreContextMenu")
    }
}
