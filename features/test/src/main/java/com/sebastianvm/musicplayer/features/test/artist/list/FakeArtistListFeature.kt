package com.sebastianvm.musicplayer.features.test.artist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature

class FakeArtistListFeature : ArtistListFeature {
    override fun artistListUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "ArtistList")
    }
}
