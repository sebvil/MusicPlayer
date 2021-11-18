package com.sebastianvm.musicplayer.ui.player

import android.os.SystemClock
import androidx.lifecycle.viewModelScope
import com.sebastianvm.commons.R
import com.sebastianvm.commons.util.DisplayableString
import com.sebastianvm.commons.util.MediaArt
import com.sebastianvm.musicplayer.player.MusicServiceConnection
import com.sebastianvm.musicplayer.ui.player.MusicPlayerUserAction.*
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.ArtLoader
import com.sebastianvm.musicplayer.util.extensions.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    initialState: MusicPlayerState,
    private val musicServiceConnection: MusicServiceConnection
) :
    BaseViewModel<MusicPlayerUserAction, MusicPlayerState>(initialState) {

    init {
        observe(musicServiceConnection.nowPlaying) {
            val trackGid = if (it.id.isNullOrEmpty()) null else it.id
            val albumGid = if (it.albumId.isNullOrEmpty()) null else it.albumId
            setState {
                copy(
                    trackName = it.title,
                    artists = it.artist,
                    trackLengthMs = it.duration,
                    trackGid = trackGid,
                    albumGid = albumGid,
                    trackArt = ArtLoader.getTrackArt(
                        trackGid?.toLong() ?: -1,
                        albumGid?.toLong() ?: -1,
                        it.title ?: ""
                    )
                )
            }
        }
        observe(musicServiceConnection.playbackState) {
            setState {
                copy(
                    isPlaying = it.isPlaying,
                )
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(100)
                updateProgress()
            }
        }
    }

    override fun handle(action: MusicPlayerUserAction) {
        val transportControls = musicServiceConnection.transportControls
        when (action) {
            is TogglePlay -> {
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
            is PreviousTapped -> {
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
            currentPosition += (timeDelta.toInt() * playbackState.playbackSpeed).toLong()
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
    val trackGid: String?,
    val albumGid: String?,
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
            trackGid = null,
            albumGid = null,
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

