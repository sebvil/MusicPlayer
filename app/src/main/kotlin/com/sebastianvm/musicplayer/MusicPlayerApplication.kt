package com.sebastianvm.musicplayer

import android.app.Application
import androidx.work.Configuration
import com.sebastianvm.musicplayer.core.services.HasServices
import com.sebastianvm.musicplayer.di.AppServices

class MusicPlayerApplication : Application(), Configuration.Provider, HasServices {

    override val services by lazy { AppServices(this) }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(MusicPlayerWorkerFactory(services)).build()
}
