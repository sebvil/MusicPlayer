package com.sebastianvm.musicplayer.features.album.details

import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsFeature
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

class DefaultAlbumDetailsFeature : AlbumDetailsFeature {
    override fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
    ): UiComponent<AlbumDetailsArguments, *> {
        return AlbumDetailsUiComponent(arguments, navController)
    }
}
