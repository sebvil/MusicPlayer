package com.sebastianvm.musicplayer.features.artist.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListFeature

class DefaultArtistListFeature : ArtistListFeature {
    override fun artistListUiComponent(navController: NavController): UiComponent<*> {
        return ArtistListUiComponent(navController)
    }
}
