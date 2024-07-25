package com.sebastianvm.musicplayer.features.api.album.list

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.Features

interface AlbumListFeature {
    fun albumListUiComponent(navController: NavController, features: Features): UiComponent<*>
}
