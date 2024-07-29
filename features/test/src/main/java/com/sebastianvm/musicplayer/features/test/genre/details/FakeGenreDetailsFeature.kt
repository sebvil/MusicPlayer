package com.sebastianvm.musicplayer.features.test.genre.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature

class FakeGenreDetailsFeature : GenreDetailsFeature {
    override fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController,
    ): UiComponent<*> {
        return FakeUiComponent(arguments, "GenreDetails")
    }
}
