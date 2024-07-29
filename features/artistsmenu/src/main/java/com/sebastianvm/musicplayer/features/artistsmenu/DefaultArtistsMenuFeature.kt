package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuArguments
import com.sebastianvm.musicplayer.features.api.artistsmenu.ArtistsMenuFeature

class DefaultArtistsMenuFeature : ArtistsMenuFeature {
    override fun artistsMenuUiComponent(
        arguments: ArtistsMenuArguments,
        navController: NavController
    ): UiComponent<*> {
        return ArtistsMenuUiComponent(arguments, navController)
    }
}
