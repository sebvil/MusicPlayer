package com.sebastianvm.musicplayer.features.test.album.menu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature

class FakeAlbumContextMenuFeature : AlbumContextMenuFeature {
    override fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): UiComponent<*> {
        return FakeUiComponent(arguments, "AlbumContextMenu")
    }
}
