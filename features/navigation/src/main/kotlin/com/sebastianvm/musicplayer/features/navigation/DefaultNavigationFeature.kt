package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature

class DefaultNavigationFeature : NavigationFeature {
    override fun navigationUiComponent(): UiComponent<*> {
        return NavigationHostUiComponent()
    }
}
