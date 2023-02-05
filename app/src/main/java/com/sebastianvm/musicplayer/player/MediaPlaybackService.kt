package com.sebastianvm.musicplayer.player

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
import com.sebastianvm.musicplayer.repository.playback.PlaybackInfoRepository
import com.sebastianvm.musicplayer.repository.playback.mediatree.MediaTree
import com.sebastianvm.musicplayer.util.coroutines.DefaultDispatcher
import com.sebastianvm.musicplayer.util.coroutines.MainDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlaybackService : MediaLibraryService() {

    @Inject
    lateinit var playbackInfoRepository: PlaybackInfoRepository

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    @DefaultDispatcher
    lateinit var defaultDispatcher: CoroutineDispatcher

    @Inject
    lateinit var mediaTree: MediaTree

    private lateinit var player: Player
    private lateinit var mediaSession: MediaLibrarySession

    private val serviceScope = MainScope() + CoroutineName("Playback Service")

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_ALL)
            .build()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true).build()

        serviceScope.launch {
            initializeQueue()
        }

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                serviceScope.launch {
                    updateQueue()
                }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                serviceScope.launch {
                    updateQueue()
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                serviceScope.launch {
                    updateQueue()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                player.seekToNext()
                player.prepare()
                player.play()
            }
        })
        mediaSession = MediaLibrarySession.Builder(
            /* service = */ this,
            /* player = */ player,
            /* callback = */ object : MediaLibrarySession.Callback {
                override fun onGetLibraryRoot(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<MediaItem>> {
                    return Futures.immediateFuture(LibraryResult.ofItem(mediaTree.root, params))
                }

                override fun onGetChildren(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    parentId: String,
                    page: Int,
                    pageSize: Int,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
                    return serviceScope.future {
                        mediaTree.getChildren(parentId).let {
                            LibraryResult.ofItemList(it, params)
                        }
                    }
                }

                override fun onGetItem(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    mediaId: String
                ): ListenableFuture<LibraryResult<MediaItem>> {
                    return serviceScope.future {
                        mediaTree.getItem(mediaId)?.let {
                            LibraryResult.ofItem(it, null)
                        } ?: LibraryResult.ofError(RESULT_ERROR_BAD_VALUE)
                    }
                }

                override fun onSubscribe(
                    session: MediaLibrarySession,
                    browser: MediaSession.ControllerInfo,
                    parentId: String,
                    params: LibraryParams?
                ): ListenableFuture<LibraryResult<Void>> {
                    return serviceScope.future {
                        val children = mediaTree.getChildren(parentId)
                        session.notifyChildrenChanged(browser, parentId, children.size, params)
                        LibraryResult.ofVoid()
                    }
                }

                override fun onAddMediaItems(
                    mediaSession: MediaSession,
                    controller: MediaSession.ControllerInfo,
                    mediaItems: MutableList<MediaItem>
                ): ListenableFuture<MutableList<MediaItem>> {
                    val newMediaItems = mediaItems.mapNotNull {
                        mediaTree.getItem(mediaId = it.mediaId)
                    }.toMutableList()
                    return Futures.immediateFuture(newMediaItems)
                }
            }).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }


    private suspend fun initializeQueue() {
        withContext(defaultDispatcher) {
            playbackInfoRepository.getSavedPlaybackInfo().first().run {
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
        serviceScope.cancel()
    }


    suspend fun updateQueue() {
        playbackInfoRepository.modifySavedPlaybackInfo(player)
    }

}
