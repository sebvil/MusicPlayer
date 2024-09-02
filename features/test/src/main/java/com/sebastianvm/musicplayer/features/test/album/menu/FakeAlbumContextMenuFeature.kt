package com.sebastianvm.musicplayer.features.test.album.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuFeature

class FakeAlbumContextMenuFeature : AlbumContextMenuFeature {
    override fun albumContextMenuUiComponent(
        arguments: AlbumContextMenuArguments,
        navController: NavController,
    ): MvvmComponent {
        return FakeMvvmComponent(arguments, "AlbumContextMenu")
    }
}
