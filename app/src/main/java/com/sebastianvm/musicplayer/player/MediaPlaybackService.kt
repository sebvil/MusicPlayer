package com.sebastianvm.musicplayer.player

import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.LibraryResult.RESULT_ERROR_BAD_VALUE
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.sebastianvm.musicplayer.database.entities.TrackWithQueueId
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.mediatree.MediaTree
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import com.sebastianvm.musicplayer.util.coroutines.MainDispatcher
import com.sebastianvm.musicplayer.util.extensions.uniqueId
import com.sebastianvm.musicplayer.util.extensions.uri
import com.sebastianvm.musicplayer.util.uri.UriUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.asListenableFuture
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlaybackService : MediaLibraryService() {

    @Inject
    lateinit var playbackManager: PlaybackManager

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    @Inject
    lateinit var mediaTree: MediaTree

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private val queue: MutableStateFlow<List<TrackWithQueueId>> = MutableStateFlow(listOf())

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
            .build()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true).build()

        CoroutineScope(mainDispatcher).launch {
            initializeQueue()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    savePlaybackInfo()
                }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    savePlaybackInfo()
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    savePlaybackInfo()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                player.seekToNext()
                player.prepare()
                player.play()
            }
        })
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            object : MediaLibrarySession.Callback {
                override fun onGetLibraryRoot(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<MediaItem>> {
                    Log.i("000Player", "get root")
                    return Futures.immediateFuture(
                        LibraryResult.ofItem(
                            mediaTree.getRoot(),
                            params
                        )
                    )
                }

                override fun onGetChildren(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    parentId: String,
                    page: Int,
                    pageSize: Int,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                    Log.i("000Player", "get parent: $parentId")
                    return mediaTree.getCachedChildren(parentId)?.let {
                        Log.i("000Player", "cached children: ${it.map { child -> child.mediaId }}")
                        Futures.immediateFuture(LibraryResult.ofItemList(it, params))
                    } ?: CoroutineScope(mainDispatcher).async {
                        mediaTree.getChildren(parentId)?.let {
                            Log.i("000Player", "children: ${it.map { child -> child.mediaId }}")
                            LibraryResult.ofItemList(it, params)
                        } ?: LibraryResult.ofError(RESULT_ERROR_BAD_VALUE)
                    }.asListenableFuture()
                }

                override fun onGetItem(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    mediaId: String
                ): ListenableFuture<LibraryResult<MediaItem>> {
                    Log.i("000Player", "get item: $mediaId")
                    return mediaTree.getCachedMediaItem(mediaId)
                        ?.let { Futures.immediateFuture(LibraryResult.ofItem(it, null)) }
                        ?: CoroutineScope(mainDispatcher).future {
                            mediaTree.getItem(mediaId)?.let {
                                LibraryResult.ofItem(it, null)
                            } ?: LibraryResult.ofError(RESULT_ERROR_BAD_VALUE)
                        }
                }

                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    val newMediaItems = mediaItems.map {
                        it.buildUpon().apply {
                            uri = UriUtils.getTrackUri(it.mediaId.toLong())
                        }.build()
                    }.toMutableList()
                    return Futures.immediateFuture(newMediaItems)
                }
            }
        ).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }

    private suspend fun initializeQueue() {
        withContext(defaultDispatcher) {
            playbackManager.getSavedPlaybackInfo().first().run {
                if (queuedTracks.isEmpty()) {
                    return@run
                }
                withContext(mainDispatcher) {
                    preparePlaylist(
                        initialWindowIndex = queuedTracks.indexOfFirst { it.uniqueQueueItemId == nowPlayingId },
                        mediaItems = queuedTracks.map { it.toMediaItem() },
                        position = lastRecordedPosition
                    )
                }
            }
        }
    }

    private fun preparePlaylist(
        initialWindowIndex: Int,
        mediaItems: List<MediaItem>,
        position: Long
    ) {
        player.apply {
            player.playWhenReady = false
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

    suspend fun savePlaybackInfo() {
        val id = player.currentMediaItem?.uniqueId ?: 0L
        val contentPosition = player.contentPosition
        playbackManager.modifySavedPlaybackInfo(
            PlaybackInfo(
                queuedTracks = queue.value,
                nowPlayingId = id,
                lastRecordedPosition = contentPosition
            )
        )
    }

    suspend fun updateQueue() {
        val timeline = player.currentTimeline
        withContext(defaultDispatcher) {
            val newQueue = (0 until timeline.windowCount).map {
                TrackWithQueueId.fromMediaItem(
                    mediaItem = timeline.getWindow(
                        it,
                        Timeline.Window()
                    ).mediaItem
                )
            }
            queue.value = newQueue
        }
    }
}
