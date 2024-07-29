package com.sebastianvm.musicplayer.features.test.home

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.home.HomeFeature

class FakeHomeFeature : HomeFeature {
    override fun homeUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(arguments = null, name = "Home")
    }
}
