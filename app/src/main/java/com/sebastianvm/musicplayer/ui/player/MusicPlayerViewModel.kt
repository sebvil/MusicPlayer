package com.sebastianvm.musicplayer.ui.player

import android.os.SystemClock
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.player.MusicPlayerUserAction.NextTapped
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
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
    private val musicServiceConnection: MusicServiceConnection,
    initialState: MusicPlayerState,
) :
    BaseViewModel<MusicPlayerUserAction, MusicPlayerUiEvent, MusicPlayerState>(initialState) {

    init {
        collect(musicServiceConnection.nowPlaying) {
            val trackId = if (it.id.isNullOrEmpty()) null else it.id
            val albumId = if (it.albumId.isNullOrEmpty()) null else it.albumId
            setState {
                copy(
                    trackName = it.title,
                    artists = it.artist,
                    trackLengthMs = it.duration,
                    trackId = trackId,
                    albumId = albumId,
                    trackArt = ArtLoader.getTrackArt(
                        trackId ?: "0",
                        albumId ?: "0",
                        it.title ?: ""
                    )
                )
            }
        }
        collect(musicServiceConnection.playbackState) {
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
        val transportControls = musicServiceConnection.transportControls
        when (action) {
            is MusicPlayerUserAction.TogglePlay -> {
                musicServiceConnection.playbackState.value.let { playbackState ->
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
            is NextTapped -> {
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
        val playbackState = musicServiceConnection.playbackState.value

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
    val trackArt: MediaArt
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
            trackArt = MediaArt(
                uris = listOf(),
                contentDescription = DisplayableString.StringValue(""),
                backupResource = R.drawable.ic_album,
                backupContentDescription = DisplayableString.ResourceValue(R.string.placeholder_album_art),
            )
        )
    }
}

sealed class MusicPlayerUserAction : UserAction {
    object TogglePlay : MusicPlayerUserAction()
    object NextTapped : MusicPlayerUserAction()
    object PreviousTapped : MusicPlayerUserAction()
}

sealed class MusicPlayerUiEvent : UiEvent
