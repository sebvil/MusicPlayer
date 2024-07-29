package com.sebastianvm.musicplayer.features.test.genre.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature

class FakeGenreListFeature : GenreListFeature {
    override fun genreListUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "GenreList")
    }
}
