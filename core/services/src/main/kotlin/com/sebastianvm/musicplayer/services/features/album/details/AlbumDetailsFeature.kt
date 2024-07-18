package com.sebastianvm.musicplayer.services.features.album.details

import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

interface AlbumDetailsFeature {
    fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController
    ): UiComponent<AlbumDetailsArguments, *>
}
