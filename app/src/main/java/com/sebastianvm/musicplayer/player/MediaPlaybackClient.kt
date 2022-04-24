package com.sebastianvm.musicplayer.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sebastianvm.musicplayer.repository.playback.MediaItemMetadata
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import com.sebastianvm.musicplayer.util.coroutines.MainDispatcher
import com.sebastianvm.musicplayer.util.extensions.duration
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlaybackClient @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    val playbackState = MutableStateFlow(
        PlaybackState(
            mediaItemMetadata = null,
            isPlaying = false,
            currentPlayTimeMs = 0,
        )
    )
    val queue: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    val nowPlaying: MutableStateFlow<MediaMetadata?> = MutableStateFlow(null)
    val currentIndex: MutableStateFlow<Int> = MutableStateFlow(-1)


    fun initializeController() {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun launchCurrentPlayTimeUpdates() {
        CoroutineScope(mainDispatcher).launch {
            while (true) {
                delay(1000)
                controller?.also {
                    if (it.isPlaying) {
                        playbackState.value =
                            playbackState.value.copy(currentPlayTimeMs = it.contentPosition)
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
                    playbackState.value = playbackState.value.copy(
                        isPlaying = isPlaying || controller.playWhenReady,
                        currentPlayTimeMs = controller.contentPosition.takeUnless { it == C.TIME_UNSET }
                            ?: 0,
                    )
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    nowPlaying.value = mediaMetadata
                    currentIndex.value = controller.currentMediaItemIndex
                    playbackState.value = playbackState.value.copy(
                        mediaItemMetadata = mediaMetadata.toMediaItemMetadata(),
                        currentPlayTimeMs = controller.contentPosition.takeUnless { it == C.TIME_UNSET }
                            ?: 0)

                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    currentIndex.value = controller.currentMediaItemIndex
                    CoroutineScope(mainDispatcher).launch {
                        updateQueue()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    next()
                    controller.prepare()
                    play()
                }
            }
        )
        launchCurrentPlayTimeUpdates()
    }


    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
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
     * TODO this should take an index instead of an id
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

    private fun MediaMetadata?.toMediaItemMetadata(): MediaItemMetadata? = this?.let {
        MediaItemMetadata(
            title = title?.toString() ?: "",
            artists = artist?.toString() ?: "",
            artworkUri = artworkUri ?: Uri.EMPTY,
            trackDurationMs = duration
        )
    }

    suspend fun updateQueue() {
        val controllerNotNull = controller ?: return
        val currentIndex = controllerNotNull.currentMediaItemIndex
        val timeline = controllerNotNull.currentTimeline
        withContext(defaultDispatcher) {
            val newQueue = (currentIndex until timeline.windowCount).map {
                timeline.getWindow(it, Timeline.Window()).mediaItem.mediaId
            }
            queue.value = newQueue
        }
    }

}