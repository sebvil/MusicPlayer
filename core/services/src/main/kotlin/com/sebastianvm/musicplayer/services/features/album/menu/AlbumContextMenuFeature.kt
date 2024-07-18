package com.sebastianvm.musicplayer.services.features.album.menu

import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

interface AlbumContextMenuFeature {
    fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): UiComponent<AlbumContextMenuArguments, *>
}
