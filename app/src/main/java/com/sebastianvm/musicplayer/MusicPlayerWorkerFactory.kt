package com.sebastianvm.musicplayer

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.sebastianvm.musicplayer.core.sync.LibrarySyncWorker
import com.sebastianvm.musicplayer.di.Dependencies

class MusicPlayerWorkerFactory(private val dependencies: Dependencies) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            LibrarySyncWorker::class.java.name -> {
                LibrarySyncWorker(
                    context = appContext,
                    workerParams = workerParameters,
                    musicRepository = dependencies.repositoryProvider.musicRepository,
                )
            }
            else -> null
        }
    }
}
