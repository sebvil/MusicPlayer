package com.sebastianvm.musicplayer.player

import android.app.Notification
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.sebastianvm.musicplayer.repository.playback.COMMAND_SEEK_TO_MEDIA_ITEM
import com.sebastianvm.musicplayer.repository.playback.EXTRA_MEDIA_INDEX
import com.sebastianvm.musicplayer.repository.playback.MEDIA_GROUP
import com.sebastianvm.musicplayer.util.extensions.MEDIA_METADATA_COMPAT_KEY
import com.sebastianvm.musicplayer.util.extensions.flags
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.mediaUri
import com.sebastianvm.musicplayer.util.extensions.move
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class OldMediaPlaybackService : MediaBrowserServiceCompat() {


    @Inject
    lateinit var browseTree: BrowseTree
    private lateinit var notificationManager: PlaybackNotificationManager

    companion object {
        private const val LOG_TAG = "MEDIA_BROWSER_SERVICE"
    }

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var currentPlayer: Player
    private var currentPlaylistItems: MutableList<MediaMetadataCompat> = mutableListOf()

    private var isForegroundService = false

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(this, LOG_TAG).apply {

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
        notificationManager =
            PlaybackNotificationManager(
                context = this,
                sessionToken = mediaSession.sessionToken,
                PlayerNotificationListener()
            )
        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(PlaybackPreparer())
        mediaSessionConnector.setQueueNavigator(QueueNavigator())
        mediaSessionConnector.setMediaMetadataProvider(MetadataProvider())
        mediaSessionConnector.setQueueEditor(
            TimelineQueueEditor(
                mediaSession.controller,
                QueueDataAdapter(),
                MediaDescriptionConverter()
            )
        )
        mediaSessionConnector.registerCustomCommandReceiver(CommandReceiver())

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()

        exoPlayer.setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true)
        switchToPlayer(
            previousPlayer = null,
            newPlayer = exoPlayer
        )
        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(BrowseTree.MEDIA_ROOT, null)
    }

    private val queriedParentIds = mutableSetOf<String>()

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            if (parentId !in queriedParentIds) {
                browseTree[parentId]?.collect {
                    queriedParentIds.add(parentId)
                    notifyChildrenChanged(parentId)
                }
            }
            browseTree[parentId]?.first { metadata ->
                result.sendResult(metadata.map {
                    MediaBrowserCompat.MediaItem(
                        it.descriptionFromMetadata(),
                        it.flags
                    )
                }.toMutableList())
                true
            }
        }

        result.detach()


    }

    private fun MediaMetadataCompat.descriptionFromMetadata(): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder().apply {
            setMediaId(this@descriptionFromMetadata.id)

            setExtras((this@descriptionFromMetadata.description.extras ?: Bundle()).apply {
                putParcelable(
                    MEDIA_METADATA_COMPAT_KEY,
                    this@descriptionFromMetadata
                )
            })
        }.build()
    }

    private fun MediaDescriptionCompat.metadataFromDescription(): MediaMetadataCompat {
        return extras?.getParcelable(MEDIA_METADATA_COMPAT_KEY)
            ?: MediaMetadataCompat.Builder().build()
    }


    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        itemToPlay: MediaMetadataCompat?,
        metadataList: List<MediaMetadataCompat>,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        // Since the playlist was probably based on some ordering (such as tracks
        // on an album), find which window index to play first so that the song the
        // user actually wants to hear plays first.
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList.toMutableList()

        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.stop()
        currentPlayer.clearMediaItems()
        if (currentPlayer == exoPlayer) {
            exoPlayer.setMediaItems(currentPlaylistItems.mapNotNull {
                it.mediaUri?.let { uri ->
                    MediaItem.Builder().setUri(uri)
                        .setMediaMetadata(MediaMetadata.Builder().setArtworkUri(uri).build())
                        .build()
                }
            })
            exoPlayer.prepare()
            exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
        }
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.stop()
                currentPlayer.clearMediaItems()
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    metadataList = currentPlaylistItems,
                    itemToPlay = currentPlaylistItems[previousPlayer.currentMediaItemIndex],
                    playWhenReady = previousPlayer.playWhenReady,
                    playbackStartPositionMs = previousPlayer.currentPosition
                )
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop()
        previousPlayer?.clearMediaItems()
    }

    private inner class PlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean = false

        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH

        override fun onPrepare(playWhenReady: Boolean) {
            return
        }

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            val queueId = extras?.getParcelable<MediaGroup>(MEDIA_GROUP)
            CoroutineScope(Dispatchers.IO).launch {
                queueId?.also {
                    browseTree.getTracksList(it).first().also { tracks ->
                        val itemToPlay = tracks.find { item -> item.id == mediaId }
                        withContext(Dispatchers.Main) {
                            preparePlaylist(
                                itemToPlay,
                                tracks,
                                playWhenReady,
                                0
                            )
                        }
                    }
                    mediaSession.setExtras(Bundle().apply { putParcelable(MEDIA_GROUP, queueId) })
                }
            }
        }

        /**
         * This method is used by the Google Assistant to respond to requests such as:
         */
        override fun onPrepareFromSearch(
            query: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) =
            Unit

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {

        }
    }

    private inner class QueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(
            player: Player,
            windowIndex: Int
        ): MediaDescriptionCompat {
            return currentPlaylistItems[windowIndex].description
        }
    }

    private inner class MetadataProvider : MediaSessionConnector.MediaMetadataProvider {
        override fun getMetadata(player: Player): MediaMetadataCompat {
            return if (currentPlaylistItems.isEmpty()) {
                MediaMetadataCompat.Builder().build()
            } else {
                currentPlaylistItems[player.currentMediaItemIndex]
            }
        }
    }

    private inner class QueueDataAdapter : TimelineQueueEditor.QueueDataAdapter {
        override fun add(position: Int, description: MediaDescriptionCompat) {
            currentPlaylistItems.add(position, description.metadataFromDescription())
        }

        override fun remove(position: Int) {
            currentPlaylistItems.removeAt(position)
        }

        override fun move(from: Int, to: Int) {
            currentPlaylistItems.move(from, to)
        }
    }

    private inner class MediaDescriptionConverter : TimelineQueueEditor.MediaDescriptionConverter {
        override fun convert(description: MediaDescriptionCompat): MediaItem? {
            return description.mediaUri?.let { MediaItem.fromUri(it) }
        }
    }

    private inner class CommandReceiver : MediaSessionConnector.CommandReceiver {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean {
            return when (command) {
                COMMAND_SEEK_TO_MEDIA_ITEM -> {
                    val index = extras?.getInt(EXTRA_MEDIA_INDEX)
                    index?.let {
                        player.seekToDefaultPosition(it)
                        true
                    } ?: false
                }
                else -> false
            }
        }

    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@OldMediaPlaybackService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }
}
