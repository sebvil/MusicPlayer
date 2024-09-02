package com.sebastianvm.musicplayer.features.test.artist.list

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature

class FakeArtistListFeature : ArtistListFeature {
    override fun artistListUiComponent(navController: NavController): MvvmComponent {
        return FakeMvvmComponent(arguments = null, name = "ArtistList")
    }
}
