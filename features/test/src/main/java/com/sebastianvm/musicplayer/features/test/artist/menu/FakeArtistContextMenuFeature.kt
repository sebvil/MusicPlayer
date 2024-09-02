package com.sebastianvm.musicplayer.features.test.artist.menu

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuArguments
import com.sebastianvm.musicplayer.features.api.artist.menu.ArtistContextMenuFeature

class FakeArtistContextMenuFeature : ArtistContextMenuFeature {
    override fun artistContextMenuUiComponent(
        arguments: ArtistContextMenuArguments
    ): MvvmComponent {
        return FakeMvvmComponent(arguments, "ArtistContextMenu")
    }
}
