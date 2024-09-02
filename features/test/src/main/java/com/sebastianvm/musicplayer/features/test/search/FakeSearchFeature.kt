package com.sebastianvm.musicplayer.features.test.search

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.api.search.SearchFeature

class FakeSearchFeature : SearchFeature {
    override fun searchUiComponent(navController: NavController): MvvmComponent {
        return FakeMvvmComponent(null, "Search")
    }
}
