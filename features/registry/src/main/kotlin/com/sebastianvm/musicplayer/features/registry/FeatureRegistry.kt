package com.sebastianvm.musicplayer.features.registry

typealias FeatureFactory = () -> Feature<*, *>

interface FeatureRegistry {
    fun <F : Feature<*, *>> featureByKey(key: Feature.Key): F

    fun register(key: Feature.Key, featureFactory: FeatureFactory)
}
