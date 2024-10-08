package com.sebastianvm.musicplayer.core.playback.player

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
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.sebastianvm.musicplayer.core.common.DispatcherNames
import com.sebastianvm.musicplayer.core.data.UriUtils
import com.sebastianvm.musicplayer.core.data.queue.QueueRepository
import com.sebastianvm.musicplayer.core.model.BasicQueuedTrack
import com.sebastianvm.musicplayer.core.model.NowPlayingInfo
import com.sebastianvm.musicplayer.core.model.QueuedTrack
import com.sebastianvm.musicplayer.core.playback.extensions.toMediaItem
import com.sebastianvm.musicplayer.core.playback.extensions.uniqueId
import com.sebastianvm.musicplayer.core.playback.extensions.uri
import com.sebastianvm.musicplayer.core.playback.mediatree.MediaTree
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.asListenableFuture
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

internal class MediaPlaybackService : MediaLibraryService() {

    private val queueRepository: QueueRepository by inject()

    private val mainDispatcher: CoroutineDispatcher by
        inject(qualifier = named(DispatcherNames.MAIN))
    private val defaultDispatcher: CoroutineDispatcher by
        inject(qualifier = named(DispatcherNames.DEFAULT))

    private val mediaTree: MediaTree by inject()

    private lateinit var player: Player
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        val audioAttributes =
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
                .build()
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
                .build()

        CoroutineScope(mainDispatcher).launch { initializeQueue() }

        player.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    CoroutineScope(mainDispatcher).launch { saveQueue() }
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    CoroutineScope(mainDispatcher).launch { saveQueue() }
                }

                override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                    CoroutineScope(mainDispatcher).launch { saveQueue() }
                }

                override fun onPlayerError(error: PlaybackException) {
                    player.seekToNext()
                    player.prepare()
                    player.play()
                }
            }
        )
        mediaSession =
            MediaLibrarySession.Builder(
                    this,
                    player,
                    object : MediaLibrarySession.Callback {
                        override fun onGetLibraryRoot(
                            session: MediaLibrarySession,
                            browser: MediaSession.ControllerInfo,
                            params: LibraryParams?,
                        ): ListenableFuture<LibraryResult<MediaItem>> {
                            Log.i("000Player", "get root")
                            return Futures.immediateFuture(
                                LibraryResult.ofItem(mediaTree.getRoot(), params)
                            )
                        }

                        override fun onGetChildren(
                            session: MediaLibrarySession,
                            browser: MediaSession.ControllerInfo,
                            parentId: String,
                            page: Int,
                            pageSize: Int,
                            params: LibraryParams?,
                        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                            Log.i("000Player", "get parent: $parentId")
                            return mediaTree.getCachedChildren(parentId)?.let {
                                Log.i(
                                    "000Player",
                                    "cached children: ${it.map { child -> child.mediaId }}",
                                )
                                Futures.immediateFuture(LibraryResult.ofItemList(it, params))
                            }
                                ?: CoroutineScope(mainDispatcher)
                                    .async {
                                        mediaTree.getChildren(parentId)?.let {
                                            Log.i(
                                                "000Player",
                                                "children: ${it.map { child -> child.mediaId }}",
                                            )
                                            LibraryResult.ofItemList(it, params)
                                        } ?: LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                                    }
                                    .asListenableFuture()
                        }

                        override fun onGetItem(
                            session: MediaLibrarySession,
                            browser: MediaSession.ControllerInfo,
                            mediaId: String,
                        ): ListenableFuture<LibraryResult<MediaItem>> {
                            Log.i("000Player", "get item: $mediaId")
                            return mediaTree.getCachedMediaItem(mediaId)?.let {
                                Futures.immediateFuture(LibraryResult.ofItem(it, null))
                            }
                                ?: CoroutineScope(mainDispatcher).future {
                                    mediaTree.getItem(mediaId)?.let {
                                        LibraryResult.ofItem(it, null)
                                    } ?: LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                                }
                        }

                        override fun onAddMediaItems(
                            mediaSession: MediaSession,
                            controller: MediaSession.ControllerInfo,
                            mediaItems: MutableList<MediaItem>,
                        ): ListenableFuture<MutableList<MediaItem>> {
                            val newMediaItems =
                                mediaItems
                                    .map {
                                        it.buildUpon()
                                            .apply {
                                                uri = UriUtils.getTrackUri(it.mediaId.toLong())
                                            }
                                            .build()
                                    }
                                    .toMutableList()
                            return Futures.immediateFuture(newMediaItems)
                        }
                    },
                )
                .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }

    private suspend fun initializeQueue() {
        withContext(defaultDispatcher) {
            val queue = queueRepository.getFullQueue().first() ?: return@withContext
            withContext(mainDispatcher) {
                preparePlaylist(
                    initialWindowIndex = queue.nowPlayingInfo.nowPlayingPositionInQueue,
                    mediaItems = queue.queue.map { it.toMediaItem() },
                    position = queue.nowPlayingInfo.lastRecordedPosition,
                )
            }
        }
    }

    private fun preparePlaylist(
        initialWindowIndex: Int,
        mediaItems: List<MediaItem>,
        position: Long,
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

    suspend fun saveQueue() {
        withContext(mainDispatcher) {
            val contentPosition = player.contentPosition
            val timeline = player.currentTimeline
            val nowPlayingPositionInQueue = player.currentMediaItemIndex
            withContext(defaultDispatcher) {
                val newQueue =
                    (0 until timeline.windowCount).map { windowIndex ->
                        timeline
                            .getWindow(windowIndex, Timeline.Window())
                            .mediaItem
                            .toBasicQueuedTrack(positionInQueue = windowIndex)
                    }
                queueRepository.saveQueue(
                    nowPlayingInfo =
                        NowPlayingInfo(
                            nowPlayingPositionInQueue = nowPlayingPositionInQueue,
                            lastRecordedPosition = contentPosition,
                        ),
                    queuedTracks = newQueue,
                )
            }
        }
    }
}

private fun MediaItem.toBasicQueuedTrack(positionInQueue: Int): BasicQueuedTrack {
    return BasicQueuedTrack(
        trackId = mediaId.toLong(),
        queuePosition = positionInQueue,
        queueItemId = uniqueId,
    )
}

fun QueuedTrack.toMediaItem(): MediaItem {
    val item = track.toMediaItem()
    return item.buildUpon().setTag(queuePosition).build()
}
