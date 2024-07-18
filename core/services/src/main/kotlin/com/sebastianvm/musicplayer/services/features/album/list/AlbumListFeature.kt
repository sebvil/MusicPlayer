package com.sebastianvm.musicplayer.services.features.album.list

import com.sebastianvm.musicplayer.services.features.mvvm.NoArguments
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.UiComponent

interface AlbumListFeature {
    fun albumListUiComponent(
        navController: NavController,
    ): UiComponent<NoArguments, *>
}
