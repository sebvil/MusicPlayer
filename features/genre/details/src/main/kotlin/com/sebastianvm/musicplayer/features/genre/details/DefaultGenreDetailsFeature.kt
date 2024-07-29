package com.sebastianvm.musicplayer.features.genre.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsArguments
import com.sebastianvm.musicplayer.features.api.genre.details.GenreDetailsFeature

class DefaultGenreDetailsFeature : GenreDetailsFeature {
    override fun genreDetailsUiComponent(
        arguments: GenreDetailsArguments,
        navController: NavController
    ): UiComponent<*> {
        return GenreDetailsUiComponent(arguments, navController)
    }
}
