package com.sebastianvm.musicplayer.features.test.artist.details

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsArguments
import com.sebastianvm.musicplayer.features.api.artist.details.ArtistDetailsFeature

class FakeArtistDetailsFeature : ArtistDetailsFeature {
    override fun artistDetailsUiComponent(
        arguments: ArtistDetailsArguments,
        navController: NavController,
    ): UiComponent<*> {
        return FakeUiComponent(arguments, "ArtistDetails")
    }
}
