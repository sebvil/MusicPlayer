package com.sebastianvm.musicplayer.player

import android.content.ComponentName
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.sebastianvm.musicplayer.database.entities.FullTrackInfo
import com.sebastianvm.musicplayer.repository.playback.PlaybackState
import com.sebastianvm.musicplayer.repository.track.TrackRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@androidx.annotation.OptIn(UnstableApi::class)
class MediaPlaybackClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackRepository: TrackRepository
) {

    private lateinit var mediaControllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    val playbackState = MutableStateFlow(PlaybackState(isPlaying = false, currentPlayTimeMs = 0, trackDurationMs = 0))
    val nowPlaying: MutableStateFlow<MediaMetadata?> = MutableStateFlow(null)


    fun initializeController() {
        val sessionToken =
            SessionToken(context, ComponentName(context, MediaPlaybackService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ setController() }, MoreExecutors.directExecutor())
        CoroutineScope(Dispatchers.Main).launch {
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

    fun releaseController() {
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun setController() {
        val controller = this.controller ?: return
        controller.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.i("PLAYER", "${controller.currentPosition}")
                    playbackState.value = playbackState.value.copy(
                        isPlaying = isPlaying,
                        currentPlayTimeMs = controller.contentPosition,
                        trackDurationMs = controller.duration
                    )
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    nowPlaying.value = mediaMetadata
                    playbackState.value = playbackState.value.copy(
                        currentPlayTimeMs = controller.currentPosition,
                        trackDurationMs = controller.duration
                    )
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                    Log.i("PLAYER", "Position discontinuity: $reason")
                }
            }
        )
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

    // TODO experiment with collect to modify queue
    fun playFromId(mediaId: String, mediaGroup: MediaGroup) {
        CoroutineScope(Dispatchers.IO).launch {
            val mediaItems = trackRepository.getTracksForQueue(mediaGroup).map { tracks ->
                tracks.map { it.toMediaItem() }
            }.first()

            withContext(Dispatchers.Main) {
                preparePlaylist(mediaId, mediaItems)
            }
        }
    }

    /**
     * Load the supplied list of songs and the song to play into the current player.
     */
    private fun preparePlaylist(
        mediaId: String,
        mediaItems: List<MediaItem>,
    ) {
        mediaItems.find { it.localConfiguration == null }
            ?.also { Log.i("PLAYLIST", "Found culprit: ${it.mediaMetadata.title}") }
        val initialWindowIndex =
            mediaItems.indexOfFirst { it.mediaId == mediaId }.takeUnless { it == -1 } ?: 0

        controller?.let { mediaController ->
            mediaController.playWhenReady = true
            mediaController.stop()
            mediaController.clearMediaItems()

            mediaController.setMediaItems(mediaItems)
            mediaController.prepare()
            mediaController.seekTo(initialWindowIndex, 0)
        }
    }


    private fun FullTrackInfo.toMediaItem(): MediaItem {
        return MediaItem.Builder().apply {
            setMediaId(track.trackId)
            setUri(
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    track.trackId.toLong()
                )
            )
            setMediaMetadata(getMediaMetadata())
        }.build()
    }

    private fun FullTrackInfo.getMediaMetadata(): MediaMetadata {
        // TODO add more metadata
        return MediaMetadata.Builder().apply {
            setTitle(track.trackName)
            setArtist(artists.joinToString(", ") { it.artistName })
            setAlbumTitle(album.albumName)
            setArtworkUri(
                ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    track.trackId.toLong()
                )
            )
        }.build()
    }

}
