package com.sebastianvm.musicplayer.player

import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@androidx.annotation.OptIn(UnstableApi::class)
class MediaPlaybackService : MediaLibraryService() {

    private lateinit var player: ExoPlayer
    @Inject
    lateinit var preferencesRepository: PreferencesRepository
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
            override fun onIsLoadingChanged(isLoading: Boolean) {
                Log.i("PLAYER", "Is loading = $isLoading")
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val contentPosition = player.contentPosition
                val id = player.currentMediaItem?.mediaMetadata?.mediaUri?.toString()?.substringAfterLast("/") ?: ""
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesRepository.modifySavedPlaybackInfo(
                        preferencesRepository.getSavedPlaybackInfo().first().copy(
                            mediaId = id,
                            lastRecordedPosition = contentPosition
                        )
                    )
                }
            }
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                val mediaId = mediaMetadata.mediaUri?.toString()?.substringAfterLast("/") ?: ""
                Log.i("QUEUE", mediaId)
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesRepository.modifySavedPlaybackInfo(
                        preferencesRepository.getSavedPlaybackInfo().first().copy(
                            mediaId = mediaId
                        )
                    )
                }
            }
        })
        mediaSession = MediaLibrarySession.Builder(
            this,
            player,
            object : MediaLibrarySession.MediaLibrarySessionCallback {})
            .setMediaItemFiller(CustomMediaItemFiller()).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibraryService.MediaLibrarySession {
        return mediaSession
    }

    private class CustomMediaItemFiller : MediaSession.MediaItemFiller {
        override fun fillInLocalConfiguration(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItem: MediaItem
        ): MediaItem {
            return MediaItem.Builder().setUri(
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mediaItem.mediaId.toLong()
                )
            ).setMediaMetadata(mediaItem.mediaMetadata).build()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        mediaSession.release()
    }
}
