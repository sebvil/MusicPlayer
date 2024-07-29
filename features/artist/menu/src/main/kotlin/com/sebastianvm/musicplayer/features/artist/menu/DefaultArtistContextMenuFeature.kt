package com.sebastianvm.musicplayer.features.artist.menu

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature

class DefaultArtistContextMenuFeature : ArtistContextMenuFeature {
    override fun artistContextMenuUiComponent(
        arguments: ArtistContextMenuArguments
    ): UiComponent<*> {
        return ArtistContextMenuUiComponent(arguments)
    }
}
