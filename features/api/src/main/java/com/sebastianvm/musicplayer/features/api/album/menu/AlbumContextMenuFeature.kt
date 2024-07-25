package com.sebastianvm.musicplayer.features.api.album.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent

interface AlbumContextMenuFeature {
    fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): UiComponent<*>
}
