package com.sebastianvm.musicplayer.features.test.album.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature

class FakeAlbumListFeature : AlbumListFeature {
    override fun albumListUiComponent(navController: NavController): MvvmComponent {
        return FakeMvvmComponent(arguments = null, name = "AlbumList")
    }
}
