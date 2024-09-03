package com.sebastianvm.musicplayer.features.test.album.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsFeature

class FakeAlbumDetailsFeature : AlbumDetailsFeature {
    override fun albumDetailsUiComponent(
        arguments: AlbumDetailsArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments, "AlbumDetails")
    }
}
