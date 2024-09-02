package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature

class DefaultNavigationFeature : NavigationFeature {
    override fun navigationUiComponent(): MvvmComponent {
        return NavigationHostMvvmComponent()
    }
}
