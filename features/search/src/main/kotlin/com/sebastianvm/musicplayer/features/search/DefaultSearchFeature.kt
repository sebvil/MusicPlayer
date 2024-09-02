package com.sebastianvm.musicplayer.features.search

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.search.SearchFeature

class DefaultSearchFeature : SearchFeature {
    override fun searchUiComponent(navController: NavController): MvvmComponent {
        return SearchMvvmComponent(navController)
    }
}
