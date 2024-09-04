package com.sebastianvm.musicplayer.features.registry

interface FeatureRegistry {
    fun register(key: Feature.Key, feature: Feature<*, *>)

    fun <F : Feature<*, *>> featureByKey(key: Feature.Key): F
}
