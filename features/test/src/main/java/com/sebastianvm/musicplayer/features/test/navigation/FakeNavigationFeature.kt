package com.sebastianvm.musicplayer.features.test.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature

class FakeNavigationFeature : NavigationFeature {
    override fun navigationUiComponent(): MvvmComponent {
        return FakeMvvmComponent(arguments = null, name = "Navigation")
    }
}
