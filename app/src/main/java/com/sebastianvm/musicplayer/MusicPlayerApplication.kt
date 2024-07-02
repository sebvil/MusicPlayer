package com.sebastianvm.musicplayer

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.work.Configuration
import com.sebastianvm.musicplayer.di.AppDependencies
import com.sebastianvm.musicplayer.features.main.MainViewModel

class MusicPlayerApplication : Application(), Configuration.Provider {

    val dependencies by lazy { AppDependencies(this) }

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
                        return MainViewModel(playbackManager = dependencies.playbackManager) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder().setWorkerFactory(MusicPlayerWorkerFactory(dependencies)).build()
}
