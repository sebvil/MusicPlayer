package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.home.HomeFeature

class DefaultHomeFeature : HomeFeature {
    override fun homeUiComponent(navController: NavController): UiComponent<*> {
        return HomeUiComponent(navController)
    }
}
