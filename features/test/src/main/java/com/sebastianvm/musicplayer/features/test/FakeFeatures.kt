package com.sebastianvm.musicplayer.features.test

import com.sebastianvm.musicplayer.core.ui.mvvm.Arguments
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.Props
import com.sebastianvm.musicplayer.core.uitest.mvvm.FakeMvvmComponent
import com.sebastianvm.musicplayer.features.registry.Feature
import com.sebastianvm.musicplayer.features.registry.FeatureFactory
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry

class FakeFeatures : FeatureRegistry {
    override fun register(key: Feature.Key, featureFactory: FeatureFactory) = Unit

    @Suppress("UNCHECKED_CAST")
    override fun <F : Feature<*, *>> featureByKey(key: Feature.Key): F {
        return FakeFeature() as F
    }
}

class FakeFeature : Feature<Arguments, Props> {
    override val initializer: MvvmComponent.Initializer<Arguments, Props> =
        MvvmComponent.Initializer { args, _ -> FakeMvvmComponent(arguments = args) }
}
