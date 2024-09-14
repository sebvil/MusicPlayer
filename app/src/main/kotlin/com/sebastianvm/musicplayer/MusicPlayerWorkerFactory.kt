package com.sebastianvm.musicplayer

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.sebastianvm.musicplayer.core.data.music.MusicRepository
import com.sebastianvm.musicplayer.core.sync.LibrarySyncWorker

class MusicPlayerWorkerFactory(private val musicRepository: MusicRepository) : WorkerFactory() {
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
                    musicRepository = musicRepository,
                )
            }
            else -> null
        }
    }
}
