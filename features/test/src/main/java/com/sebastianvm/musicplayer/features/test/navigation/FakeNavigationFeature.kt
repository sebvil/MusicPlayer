package com.sebastianvm.musicplayer.features.test.navigation

import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature

class FakeNavigationFeature : NavigationFeature {
    override fun navigationUiComponent(): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "Navigation")
    }
}
