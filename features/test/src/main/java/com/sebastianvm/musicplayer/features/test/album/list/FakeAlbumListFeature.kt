package com.sebastianvm.musicplayer.features.test.album.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListFeature

class FakeAlbumListFeature : AlbumListFeature {
    override fun albumListUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "AlbumList")
    }
}
