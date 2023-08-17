package com.sebastianvm.musicplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sebastianvm.musicplayer.repository.playback.NotPlayingState
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.repository.playback.TrackInfo
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.util.extensions.duration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class MediaPlaybackClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val externalScope: CoroutineScope,
) {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(NotPlayingState)
    val playbackState
        get() = _playbackState

    private var timeUpdatesJob: Job? = null

    private fun updatePlaybackState() {
        _playbackState.update {
            controller?.let { nonNullController ->
                if (nonNullController.mediaMetadata == MediaMetadata.EMPTY) {
                    NotPlayingState
                } else {
                    TrackPlayingState(
                        trackInfo = TrackInfo(
                            title = nonNullController.mediaMetadata.title?.toString().orEmpty(),
                            artists = nonNullController.mediaMetadata.artist?.toString().orEmpty(),
                            artworkUri = nonNullController.mediaMetadata.artworkUri?.toString()
                                .orEmpty(),
                            trackLength = nonNullController.mediaMetadata.duration.milliseconds
                        ),
                        isPlaying = nonNullController.isPlaying,
                        currentPlayTime = (
                                nonNullController.contentPosition.takeUnless { it == C.TIME_UNSET }
                                    ?: 0L
                                ).milliseconds
                    )
                }
            } ?: NotPlayingState
        }
    }

    fun initializeController() {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture)
        timeUpdatesJob?.cancel()
        timeUpdatesJob = null
    }

    private fun launchCurrentPlayTimeUpdates() {
        externalScope.launch {
            while (true) {
                @Suppress("MagicNumber")
                delay(500)
                if (controller?.isPlaying == true) {
                    updatePlaybackState()
                }
            }
        }
    }

    private fun setController() {
        val controller = this.controller ?: return
        controller.addListener(
            object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    updatePlaybackState()
                }
            }
        )
        launchCurrentPlayTimeUpdates()
    }

    fun togglePlay() {
        if (controller?.isPlaying == true) {
            controller?.pause()
        } else {
            controller?.play()
        }
    }

    fun next() {
        controller?.seekToNext()
    }

    fun prev() {
        controller?.seekToPrevious()
    }

    fun moveQueueItem(currentIndex: Int, newIndex: Int) {
        controller?.moveMediaItem(currentIndex, newIndex)
    }

    fun playQueueItem(index: Int) {
        controller?.seekToDefaultPosition(index)
        controller?.play()
    }

    fun playMediaItems(
        initialWindowIndex: Int,
        mediaItems: List<MediaItem>,
        playWhenReady: Boolean = true,
        position: Long = 0
    ) {
        preparePlaylist(initialWindowIndex, mediaItems, playWhenReady, position)
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        initialWindowIndex: Int,
        mediaItems: List<MediaItem>,
        playWhenReady: Boolean,
        position: Long
    ) {
        controller?.let { mediaController ->
            mediaController.playWhenReady = playWhenReady
            mediaController.stop()
            mediaController.clearMediaItems()

            mediaController.setMediaItems(mediaItems)
            mediaController.prepare()
            mediaController.seekTo(initialWindowIndex, position)
        }
    }

    fun addToQueue(mediaItems: List<MediaItem>): Int {
        return controller?.let { controllerNotNull ->
            val nextIndex = controllerNotNull.nextMediaItemIndex
            if (nextIndex == C.INDEX_UNSET) {
                controllerNotNull.addMediaItems(mediaItems)
            } else {
                controllerNotNull.addMediaItems(nextIndex, mediaItems)
            }
            nextIndex
        } ?: -1
    }

    fun seekToTrackPosition(position: Long) {
        controller?.also { controllerNotNull ->
            controllerNotNull.seekTo(position)
        }
    }
}
