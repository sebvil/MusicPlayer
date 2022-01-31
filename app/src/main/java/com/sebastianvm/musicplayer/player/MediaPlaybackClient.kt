package com.sebastianvm.musicplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@androidx.annotation.OptIn(UnstableApi::class)
class MediaPlaybackClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackRepository: TrackRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    val playbackState = MutableStateFlow(
        PlaybackState(
            isPlaying = false,
            currentPlayTimeMs = 0,
        )
    )
    val nowPlaying: MutableStateFlow<MediaMetadata?> = MutableStateFlow(null)
    val currentIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    private lateinit var savedPlaybackInfo: StateFlow<SavedPlaybackInfo>


    fun initializeController() {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun prepareClient() {
        CoroutineScope(Dispatchers.Main).launch {
            savedPlaybackInfo = preferencesRepository.getSavedPlaybackInfo()
                .stateIn(CoroutineScope(Dispatchers.IO))
            controller?.also {
                with(savedPlaybackInfo.value) {
                    if (it.isPlaying) {
                        playbackState.value = PlaybackState(
                            isPlaying = it.isPlaying,
                            currentPlayTimeMs = it.contentPosition,
                        )
                        nowPlaying.value = controller?.mediaMetadata
                    } else if (currentQueue.mediaGroupType != MediaGroupType.UNKNOWN) {
                        playbackState.value = PlaybackState(
                            isPlaying = it.isPlaying,
                            currentPlayTimeMs = lastRecordedPosition,
                        )
                        playFromId(
                            mediaId = mediaId,
                            mediaGroup = currentQueue,
                            playWhenReady = false,
                            position = lastRecordedPosition
                        )
                    }
                }
            }
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
                        currentPlayTimeMs = controller.currentPosition.takeUnless { it == C.TIME_UNSET }
                            ?: 0,
                    )
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    nowPlaying.value = mediaMetadata
                    currentIndex.value = controller.currentMediaItemIndex
                    playbackState.value = playbackState.value.copy(
                        currentPlayTimeMs = controller.currentPosition.takeUnless { it == C.TIME_UNSET }
                            ?: 0)

                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    currentIndex.value = controller.currentMediaItemIndex
                }

                override fun onPlayerError(error: PlaybackException) {
                    next()
                    controller.prepare()
                    play()
                }
            }
        )
        prepareClient()
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

    fun playFromId(
        mediaId: String,
        mediaGroup: MediaGroup,
        playWhenReady: Boolean = true,
        position: Long = 0
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val mediaItems = trackRepository.getTracksForQueue(mediaGroup).map { tracks ->
                tracks.map { it.toMediaItem() }
            }.first()

            preferencesRepository.modifySavedPlaybackInfo {
                SavedPlaybackInfo(
                    currentQueue = mediaGroup,
                    mediaId = mediaId,
                    lastRecordedPosition = position
                )
            }

            withContext(Dispatchers.Main) {
                preparePlaylist(mediaId, mediaItems, playWhenReady, position)
            }
        }
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        mediaId: String,
        mediaItems: List<MediaItem>,
        playWhenReady: Boolean,
        position: Long
    ) {
        val initialWindowIndex =
            mediaItems.indexOfFirst { it.mediaId == mediaId }.takeUnless { it == -1 } ?: 0

        controller?.let { mediaController ->
            mediaController.playWhenReady = playWhenReady
            mediaController.stop()
            mediaController.clearMediaItems()

            mediaController.setMediaItems(mediaItems)
            mediaController.prepare()
            mediaController.seekTo(initialWindowIndex, position)
        }
    }

    suspend fun addToQueue(mediaIds: List<String>): Int {
        return withContext(Dispatchers.Main) {
            controller?.let { controllerNotNull ->
                val tracks = trackRepository.getTracks(mediaIds).first().map { it.toMediaItem() }
                val nextIndex = controllerNotNull.nextMediaItemIndex
                if (nextIndex == C.INDEX_UNSET) {
                    controllerNotNull.addMediaItems(tracks)
                } else {
                    controllerNotNull.addMediaItems(nextIndex, tracks)
                }
                nextIndex
            } ?: -1
        }
    }

    fun seekToTrackPosition(position: Long) {
        controller?.also { controllerNotNull ->
            controllerNotNull.seekTo(position)
        }
    }
}
