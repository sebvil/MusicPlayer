package com.sebastianvm.musicplayer.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.sebastianvm.musicplayer.repository.playback.PlaybackInfoDataSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlaybackService : MediaLibraryService() {

    @Inject
    lateinit var playbackInfoDataSource: PlaybackInfoDataSource
    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus= */ true).build()
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val contentPosition = player.contentPosition
                val id = player.currentMediaItem?.mediaId ?: ""
                CoroutineScope(Dispatchers.IO).launch {
                    playbackInfoDataSource.modifySavedPlaybackInfo { savedPlaybackInfo ->
                        savedPlaybackInfo.copy(
                            mediaId = id,
                            lastRecordedPosition = contentPosition
                        )
                    }
                }
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                val mediaId = mediaMetadata.mediaUri?.toString()?.substringAfterLast("/") ?: ""
                val contentPosition = player.contentPosition
                CoroutineScope(Dispatchers.IO).launch {
                    playbackInfoDataSource.modifySavedPlaybackInfo { savedPlaybackInfo ->
                        savedPlaybackInfo.copy(
                            mediaId = mediaId,
                            lastRecordedPosition = contentPosition
                        )
                    }
                }
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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release()
    }
}
