package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.Features
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature

class DefaultAlbumListFeature : AlbumListFeature {
    override fun albumListUiComponent(
        navController: NavController,
        features: Features
    ): UiComponent<*> {
        return AlbumListUiComponent(navController = navController, features = features)
    }
}
