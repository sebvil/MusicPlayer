package com.sebastianvm.musicplayer.features.test.home

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.home.HomeFeature

class FakeHomeFeature : HomeFeature {
    override fun homeUiComponent(navController: NavController): MvvmComponent<*, *, *> {
        return FakeMvvmComponent(arguments = null, name = "Home")
    }
}
