package com.sebastianvm.musicplayer.features.registry

class DefaultFeatureRegistry : FeatureRegistry {

    private val features = mutableMapOf<Feature.Key, Feature>()

    override fun register(key: Feature.Key, feature: Feature) {
        features[key] = feature
    }

    @Suppress("UNCHECKED_CAST")
    override fun <F : Feature> featureByKey(key: Feature.Key): F {
        return features[key] as F
    }
}
