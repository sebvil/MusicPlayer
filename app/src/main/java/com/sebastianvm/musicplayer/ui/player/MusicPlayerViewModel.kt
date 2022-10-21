package com.sebastianvm.musicplayer.ui.player

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val playbackManager: PlaybackManager,
    initialState: MusicPlayerState,
) : BaseViewModel<MusicPlayerState, MusicPlayerUserAction, MusicPlayerUiEvent>(initialState) {

    init {
        playbackManager.playbackState.onEach {
            setState {
                copy(
                    trackName = it.mediaItemMetadata?.title,
                    artists = it.mediaItemMetadata?.artists,
                    trackArt = it.mediaItemMetadata?.artworkUri ?: "",
                    trackLengthMs = it.mediaItemMetadata?.trackDurationMs,
                    isPlaying = it.isPlaying,
                    currentPlaybackTimeMs = it.currentPlayTimeMs,
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: MusicPlayerUserAction) {
        when (action) {
            is MusicPlayerUserAction.PlayToggled -> {
                if (state.isPlaying) {
                    playbackManager.pause()
                } else {
                    playbackManager.play()
                }
            }

            is MusicPlayerUserAction.NextButtonClicked -> playbackManager.next()

            is MusicPlayerUserAction.PreviousButtonClicked -> playbackManager.prev()

            is MusicPlayerUserAction.ProgressBarClicked -> {
                val time: Long = (state.trackLengthMs ?: 0) * action.position / 100
                playbackManager.seekToTrackPosition(time)
            }
        }
    }
}

data class MusicPlayerState(
    val isPlaying: Boolean,
    val trackName: String?,
    val artists: String?,
    val trackLengthMs: Long?,
    val currentPlaybackTimeMs: Long?,
    val trackArt: String
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
            trackArt = "",
        )
    }
}

sealed interface MusicPlayerUiEvent : UiEvent
sealed interface MusicPlayerUserAction : UserAction {
    object PlayToggled : MusicPlayerUserAction
    object NextButtonClicked : MusicPlayerUserAction
    object PreviousButtonClicked : MusicPlayerUserAction
    data class ProgressBarClicked(val position: Int) : MusicPlayerUserAction
}