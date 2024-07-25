package com.sebastianvm.musicplayer

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.work.Configuration
import com.sebastianvm.musicplayer.core.services.HasServices
import com.sebastianvm.musicplayer.di.AppServices
import com.sebastianvm.musicplayer.di.DefaultFeatures
import com.sebastianvm.musicplayer.features.main.MainViewModel

class MusicPlayerApplication : Application(), Configuration.Provider, HasServices {

    override val services by lazy { AppServices(this) }

    val viewModelFactory: AbstractSavedStateViewModelFactory =
        object : AbstractSavedStateViewModelFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle,
            ): T {
                when (modelClass) {
                    MainViewModel::class.java -> {
                        return MainViewModel(
                            playbackManager = services.playbackManager,
                            features = DefaultFeatures())
                            as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(MusicPlayerWorkerFactory(services)).build()
}
