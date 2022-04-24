package com.sebastianvm.musicplayer.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.sebastianvm.musicplayer.database.entities.Track
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import com.sebastianvm.musicplayer.util.extensions.toMediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlaybackService : MediaLibraryService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private val queue: MutableStateFlow<List<Track>> = MutableStateFlow(listOf())


    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true).build()
        CoroutineScope(Dispatchers.Main).launch {
            initializeQueue()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                savePlaybackInfo()
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                savePlaybackInfo()
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                updateQueue()
                savePlaybackInfo()
            }
        })
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            object : MediaLibrarySession.MediaLibrarySessionCallback {})
            .setMediaItemFiller(CustomMediaItemFiller()).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }

    private class CustomMediaItemFiller : MediaSession.MediaItemFiller {
        override fun fillInLocalConfiguration(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItem: MediaItem
        ): MediaItem {
            return MediaItem.Builder()
                .setUri(mediaItem.mediaMetadata.mediaUri)
                .setMediaId(mediaItem.mediaId)
                .setMediaMetadata(mediaItem.mediaMetadata).build()
        }
    }

    private suspend fun initializeQueue() {
        playbackManager.getSavedPlaybackInfo().first().run {
            if (queuedTracks.isEmpty()) {
                return
            }

            preparePlaylist(
                initialWindowIndex = nowPlayingIndex,
                mediaItems = queuedTracks.map { it.toMediaItem() },
                playWhenReady = false,
                position = lastRecordedPosition
            )
        }
    }

    private fun preparePlaylist(
        initialWindowIndex: Int,
        mediaItems: List<MediaItem>,
        playWhenReady: Boolean,
        position: Long
    ) {
        player.apply {
            player.playWhenReady = playWhenReady
            stop()
            clearMediaItems()

            setMediaItems(mediaItems)
            prepare()
            seekTo(initialWindowIndex, position)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release()
    }

    fun savePlaybackInfo() {
        val index = player.currentMediaItemIndex
        val contentPosition = player.contentPosition
        CoroutineScope(Dispatchers.Main).launch {
            playbackManager.modifySavedPlaybackInfo(
                PlaybackInfo(
                    queuedTracks = queue.value,
                    nowPlayingIndex = index,
                    lastRecordedPosition = contentPosition
                )
            )

        }
    }

    fun updateQueue() {
        val timeline = player.currentTimeline
        CoroutineScope(defaultDispatcher).launch {
            val newQueue = (0 until timeline.windowCount).map {
                Track.fromId(trackId = timeline.getWindow(it, Timeline.Window()).mediaItem.mediaId)
            }
            queue.value = newQueue
        }
    }


}
