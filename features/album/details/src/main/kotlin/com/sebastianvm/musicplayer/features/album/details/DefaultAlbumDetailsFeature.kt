package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature

class DefaultAlbumDetailsFeature : AlbumDetailsFeature {
    override fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
    ): UiComponent<*> {
        return AlbumDetailsUiComponent(arguments = arguments, navController = navController)
    }
}
