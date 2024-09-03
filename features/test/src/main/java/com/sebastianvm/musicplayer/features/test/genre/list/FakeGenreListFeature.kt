package com.sebastianvm.musicplayer.features.test.genre.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature

class FakeGenreListFeature : GenreListFeature {
    override fun genreListUiComponent(navController: NavController): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments = null, name = "GenreList")
    }
}
