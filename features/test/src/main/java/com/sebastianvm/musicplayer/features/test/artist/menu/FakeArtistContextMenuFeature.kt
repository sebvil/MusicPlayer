package com.sebastianvm.musicplayer.features.test.artist.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature

class FakeArtistContextMenuFeature : ArtistContextMenuFeature {
    override fun artistContextMenuUiComponent(
        arguments: ArtistContextMenuArguments
    ): UiComponent<*> {
        return FakeUiComponent(arguments, "ArtistContextMenu")
    }
}
