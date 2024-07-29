package com.sebastianvm.musicplayer.features.test.search

import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeUiComponent
import com.sebastianvm.musicplayer.features.api.search.SearchFeature

class FakeSearchFeature : SearchFeature {
    override fun searchUiComponent(navController: NavController): UiComponent<*> {
        return FakeUiComponent(null, "Search")
    }
}
