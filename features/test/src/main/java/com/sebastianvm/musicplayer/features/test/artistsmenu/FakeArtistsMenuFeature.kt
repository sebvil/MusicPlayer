package com.sebastianvm.musicplayer.features.test.artistsmenu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature

class FakeArtistsMenuFeature : ArtistsMenuFeature {

    override fun artistsMenuUiComponent(
        arguments: ArtistsMenuArguments,
        navController: NavController,
    ): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments, "ArtistsMenu")
    }
}
