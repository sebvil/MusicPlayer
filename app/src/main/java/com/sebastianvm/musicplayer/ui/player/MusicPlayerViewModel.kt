package com.sebastianvm.musicplayer.ui.player

import android.content.ContentUris
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playback.PlaybackServiceRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.extensions.albumId
import com.sebastianvm.musicplayer.util.extensions.artist
import com.sebastianvm.musicplayer.util.extensions.duration
import com.sebastianvm.musicplayer.util.extensions.id
import com.sebastianvm.musicplayer.util.extensions.isPlayEnabled
import com.sebastianvm.musicplayer.util.extensions.isPlaying
import com.sebastianvm.musicplayer.util.extensions.title
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val playbackServiceRepository: PlaybackServiceRepository,
    initialState: MusicPlayerState,
) :
    BaseViewModel<MusicPlayerUserAction, MusicPlayerUiEvent, MusicPlayerState>(initialState) {

    init {
        collect(playbackServiceRepository.nowPlaying) {
            val trackId = if (it.id.isNullOrEmpty()) null else it.id
            val albumId = if (it.albumId.isNullOrEmpty()) null else it.albumId
            setState {
                copy(
                    trackName = it.title,
                    artists = it.artist,
                    trackLengthMs = it.duration,
                    trackId = trackId,
                    albumId = albumId,
                    trackArt = trackId?.let { id -> ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id.toLong()) } ?: Uri.EMPTY
                )
            }
        }
        collect(playbackServiceRepository.playbackState) {
            setState {
                copy(
                    isPlaying = it.isPlaying,
                )
            }
        }

        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                while (true) {
                    delay(1)
                    updateProgress()
                }
            }
        }
    }

    override fun handle(action: MusicPlayerUserAction) {
        val transportControls = playbackServiceRepository.transportControls
        when (action) {
            is MusicPlayerUserAction.TogglePlay -> {
                playbackServiceRepository.playbackState.value.let { playbackState ->
                    when {
                        playbackState.isPlaying -> {
                            transportControls.pause()
                        }
                        playbackState.isPlayEnabled -> {
                            transportControls.play()
                        }
                    }
                }
            }
            is MusicPlayerUserAction.PreviousTapped -> {
                transportControls.skipToPrevious()
            }
            is MusicPlayerUserAction.NextTapped -> {
                transportControls.skipToNext()
            }
        }
    }

    private fun updateProgress() {
        if (state.value.trackLengthMs == null) {
            setState {
                copy(
                    currentPlaybackTimeMs = null
                )
            }
            return
        }
        val playbackState = playbackServiceRepository.playbackState.value

        var currentPosition: Long = playbackState.position
        if (state.value.isPlaying) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            val timeDelta: Long = SystemClock.elapsedRealtime() -
                    playbackState.lastPositionUpdateTime
            currentPosition += (timeDelta * playbackState.playbackSpeed).toLong()
        }

        setState {
            copy(
                currentPlaybackTimeMs = currentPosition
            )
        }


    }
}

data class MusicPlayerState(
    val isPlaying: Boolean,
    val trackName: String?,
    val artists: String?,
    val trackLengthMs: Long?,
    val currentPlaybackTimeMs: Long?,
    val trackId: String?,
    val albumId: String?,
    val trackArt: Uri
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialMusicPlayerStateModule {
    @Provides
    @ViewModelScoped
    fun initialMusicPlayerStateProvider(): MusicPlayerState {
        return MusicPlayerState(
            isPlaying = false,
            trackName = null,
            artists = null,
            trackLengthMs = null,
            currentPlaybackTimeMs = null,
            trackId = null,
            albumId = null,
            trackArt = Uri.EMPTY
        )
    }
}

sealed class MusicPlayerUserAction : UserAction {
    object TogglePlay : MusicPlayerUserAction()
    object NextTapped : MusicPlayerUserAction()
    object PreviousTapped : MusicPlayerUserAction()
}

sealed class MusicPlayerUiEvent : UiEvent
