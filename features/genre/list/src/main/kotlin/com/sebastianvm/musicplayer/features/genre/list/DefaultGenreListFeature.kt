package com.sebastianvm.musicplayer.features.genre.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListFeature

class DefaultGenreListFeature : GenreListFeature {
    override fun genreListUiComponent(navController: NavController): UiComponent<*> {
        return GenreListUiComponent(navController)
    }
}
