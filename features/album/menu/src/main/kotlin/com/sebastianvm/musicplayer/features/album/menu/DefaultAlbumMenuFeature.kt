package com.sebastianvm.musicplayer.features.album.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature

class DefaultAlbumContextMenuFeature : AlbumContextMenuFeature {
    override fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController
    ): UiComponent<*> {
        return AlbumContextMenuUiComponent(arguments = arguments, navController = navController)
    }
}
