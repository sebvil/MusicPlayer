package com.sebastianvm.musicplayer.features.navigation

import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.features.api.navigation.NavigationFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class DefaultNavigationFeature(private val features: FeatureRegistry) : NavigationFeature {
    override fun navigationUiComponent(): MvvmComponent {
        return NavigationHostMvvmComponent(features = features)
    }
}
