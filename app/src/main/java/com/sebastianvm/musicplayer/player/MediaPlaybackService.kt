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
import com.sebastianvm.musicplayer.MusicPlayerApplication
import com.sebastianvm.musicplayer.database.entities.QueuedTrack
import com.sebastianvm.musicplayer.model.NowPlayingInfo
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.mediatree.MediaTree
import com.sebastianvm.musicplayer.repository.queue.QueueRepository
import com.sebastianvm.musicplayer.util.extensions.uri
import com.sebastianvm.musicplayer.util.uri.UriUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.asListenableFuture
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaPlaybackService : MediaLibraryService() {

    private val dependencies by lazy {
        (application as MusicPlayerApplication).dependencies
    }

    private val playbackManager: PlaybackManager by lazy {
        dependencies.repositoryProvider.playbackManager
    }

    private val queueRepository: QueueRepository by lazy {
        dependencies.repositoryProvider.queueRepository
    }

    private val mainDispatcher: CoroutineDispatcher by lazy {
        dependencies.dispatcherProvider.mainDispatcher
    }

    private val defaultDispatcher: CoroutineDispatcher by lazy {
        dependencies.dispatcherProvider.defaultDispatcher
    }

    private val mediaTree: MediaTree by lazy {
        MediaTree(
            artistRepository = dependencies.repositoryProvider.artistRepository,
            trackRepository = dependencies.repositoryProvider.trackRepository,
            albumRepository = dependencies.repositoryProvider.albumRepository
        )
    }

    private lateinit var player: Player
    private lateinit var mediaSession: MediaLibrarySession
    private val queue: MutableStateFlow<List<QueuedTrack>> = MutableStateFlow(listOf())

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL).build()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true).build()

        CoroutineScope(mainDispatcher).launch {
            initializeQueue()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    saveQueue()
                }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    saveQueue()
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                CoroutineScope(mainDispatcher).launch {
                    updateQueue()
                    saveQueue()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                player.seekToNext()
                player.prepare()
                player.play()
            }
        })
        mediaSession =
            MediaLibrarySession.Builder(
                this, player,
                object : MediaLibrarySession.Callback {
                    override fun onGetLibraryRoot(
                        session: MediaLibrarySession,
                        browser: MediaSession.ControllerInfo,
                        params: LibraryParams?
                    ): ListenableFuture<LibraryResult<MediaItem>> {
                        Log.i("000Player", "get root")
                        return Futures.immediateFuture(
                            LibraryResult.ofItem(
                                mediaTree.getRoot(), params
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
                            Log.i(
                                "000Player",
                                "cached children: ${it.map { child -> child.mediaId }}"
                            )
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
            val queue = queueRepository.getFullQueue().first() ?: return@withContext
            withContext(mainDispatcher) {
                preparePlaylist(
                    initialWindowIndex = queue.nowPlayingInfo.nowPlayingPositionInQueue,
                    mediaItems = queue.queue.map { it.toMediaItem() },
                    position = queue.nowPlayingInfo.lastRecordedPosition
                )
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

    suspend fun saveQueue() {
        val contentPosition = player.contentPosition
        queueRepository.saveQueue(
            nowPlayingInfo = NowPlayingInfo(
                nowPlayingPositionInQueue = player.currentMediaItemIndex,
                lastRecordedPosition = contentPosition,
            ),
            queuedTracksIds = queue.value
        )
    }

    suspend fun updateQueue() {
        val timeline = player.currentTimeline
        withContext(defaultDispatcher) {
            val newQueue = (0 until timeline.windowCount).map { windowIndex ->
                QueuedTrack.fromMediaItem(
                    mediaItem = timeline.getWindow(
                        windowIndex,
                        Timeline.Window()
                    ).mediaItem,
                    positionInQueue = windowIndex
                )
            }
            queue.value = newQueue
        }
    }
}
