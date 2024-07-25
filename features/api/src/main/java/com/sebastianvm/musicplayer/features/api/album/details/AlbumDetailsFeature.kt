package com.sebastianvm.musicplayer.features.api.album.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.Features

interface AlbumDetailsFeature {
    fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
        features: Features,
    ): UiComponent<*>
}
