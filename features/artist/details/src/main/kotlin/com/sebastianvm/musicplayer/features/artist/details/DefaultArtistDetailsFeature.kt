package com.sebastianvm.musicplayer.features.artist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature

class DefaultArtistDetailsFeature : ArtistDetailsFeature {
    override fun artistDetailsUiComponent(
        arguments: ArtistDetailsArguments,
        navController: NavController
    ): UiComponent<*> {
        return ArtistDetailsUiComponent(arguments, navController)
    }
}
