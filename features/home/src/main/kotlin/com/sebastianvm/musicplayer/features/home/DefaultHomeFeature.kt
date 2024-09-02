package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.home.HomeFeature

class DefaultHomeFeature : HomeFeature {
    override fun homeUiComponent(navController: NavController): MvvmComponent {
        return HomeMvvmComponent(navController)
    }
}
