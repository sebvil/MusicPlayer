package com.sebastianvm.musicplayer

import android.app.Application
import androidx.work.Configuration
import com.sebastianvm.musicplayer.core.services.HasServices
import com.sebastianvm.musicplayer.di.AppComponent
import com.sebastianvm.musicplayer.di.create
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.HasFeatures
import kotlinx.coroutines.Dispatchers

class MusicPlayerApplication : Application(), Configuration.Provider, HasServices, HasFeatures {

    @Suppress("InjectDispatcher")
    override val services by lazy { AppComponent::class.create(this, Dispatchers.IO) }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder()
                .setWorkerFactory(MusicPlayerWorkerFactory(services.musicRepository))
                .build()

    override val featureRegistry: FeatureRegistry
        get() = services.features
}
