package com.sebastianvm.musicplayer.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
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
import com.sebastianvm.musicplayer.util.extensions.orZero
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MediaPlaybackClient(
    private val context: Context,
    private val externalScope: CoroutineScope,
) {
    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val _playbackState: MutableStateFlow<PlaybackState> = MutableStateFlow(NotPlayingState)
    val playbackState
        get() = _playbackState

    private var isUpdatingPosition = false

    private var timeUpdatesJob: Job? = null

    private fun updatePlaybackState() {
        _playbackState.update {
            controller?.let { nonNullController ->
                getUpdatedPlaybackState(nonNullController)
            } ?: NotPlayingState
        }
    }

    private fun getUpdatedPlaybackState(controller: MediaController): PlaybackState {
        return when {
            controller.mediaMetadata == MediaMetadata.EMPTY -> {
                NotPlayingState
            }

            controller.playbackState != Player.STATE_READY && playbackState.value is NotPlayingState -> {
                NotPlayingState
            }

            else -> {
                TrackPlayingState(
                    trackInfo = TrackInfo(
                        title = controller.mediaMetadata.title?.toString().orEmpty(),
                        artists = controller.mediaMetadata.artist?.toString()
                            .orEmpty(),
                        artworkUri = controller.mediaMetadata.artworkUri?.toString()
                            .orEmpty(),
                        trackLength = controller.mediaMetadata.duration.milliseconds
                    ),
                    isPlaying = controller.isPlaying,
                    currentTrackProgress = controller.contentPosition.takeUnless { it == C.TIME_UNSET }
                        .orZero().milliseconds
                )
            }
        }
    }

    fun initializeController() {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())

        Log.i("Playback", "Initializing Controller, future = $mediaControllerFuture")
    }

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture)
        timeUpdatesJob?.cancel()
        timeUpdatesJob = null
    }

    private fun launchCurrentPlayTimeUpdates() {
        externalScope.launch {
            while (true) {
                delay(500)
                if (!isUpdatingPosition) {
                    playbackState.update {
                        controller?.let { getUpdatedPlaybackState(it) } ?: NotPlayingState
                    }
                }
            }
        }
    }

    private fun setController() {
        val controller = this.controller ?: return
        controller.addListener(
            object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.i("PlaybackClient", "isPlayingChanged(isPlaying = $isPlaying)")
                    if (isUpdatingPosition) return
                    updatePlaybackState()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.i(
                        "PlaybackClient",
                        "onPlaybackStateChanged(playbackState = $playbackState)"
                    )
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    Log.i(
                        "PlaybackClient",
                        "onMediaMetadataChanged(mediaMetadata = $mediaMetadata)"
                    )
                    updatePlaybackState()
                }

                override fun onIsLoadingChanged(isLoading: Boolean) {
                    Log.i("PlaybackClient", "onIsLoadingChanged(isLoading = $isLoading)")
                    if (!isLoading) {
                        isUpdatingPosition = false
                    }
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
        isUpdatingPosition = true
        playbackState.update {
            (it as? TrackPlayingState)?.copy(currentTrackProgress = position.milliseconds)
                ?: NotPlayingState
        }
        controller?.also { controllerNotNull ->
            controllerNotNull.seekTo(position)
        }
    }
}
