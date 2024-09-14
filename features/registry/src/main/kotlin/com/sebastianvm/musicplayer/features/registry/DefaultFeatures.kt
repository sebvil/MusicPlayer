package com.sebastianvm.musicplayer.features.registry

import org.koin.core.annotation.Single

@Single
class DefaultFeatures : FeatureRegistry {

    private val features = mutableMapOf<Feature.Key, FeatureFactory>()

    override fun register(key: Feature.Key, featureFactory: FeatureFactory) {
        features[key] = featureFactory
    }

    @Suppress("UNCHECKED_CAST")
    override fun <F : Feature<*, *>> featureByKey(key: Feature.Key): F {
        return features[key]?.invoke() as F
    }
}
