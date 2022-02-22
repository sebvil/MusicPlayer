package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.duration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val mediaPlaybackRepository: MediaPlaybackRepository,
    initialState: MusicPlayerState,
) :
    BaseViewModel<MusicPlayerUiEvent, MusicPlayerState>(initialState) {

    init {
        collect(mediaPlaybackRepository.playbackState) {
            setState {
                copy(
                    isPlaying = it.isPlaying,
                    currentPlaybackTimeMs = it.currentPlayTimeMs,
                )
            }
        }

        collect(mediaPlaybackRepository.nowPlaying) { mediaMetadata ->
            setState {
                copy(
                    trackName = mediaMetadata?.title?.toString(),
                    artists = mediaMetadata?.artist?.toString(),
                    trackArt = mediaMetadata?.artworkUri?.toString() ?: "",
                    trackLengthMs = mediaMetadata?.duration
                )
            }
        }
    }

    fun <A: UserAction> handle(action: A) {
        when (action) {
            is MusicPlayerUserAction.TogglePlay -> {
                if (state.value.isPlaying) {
                    mediaPlaybackRepository.pause()
                } else {
                    mediaPlaybackRepository.play()
                }
            }
            is MusicPlayerUserAction.PreviousTapped -> {
                mediaPlaybackRepository.prev()
            }
            is MusicPlayerUserAction.NextTapped -> {
                mediaPlaybackRepository.next()
            }
            is MusicPlayerUserAction.ProgressTapped -> {
                val time: Long = (state.value.trackLengthMs ?: 0) * action.position / 100
                mediaPlaybackRepository.seekToTrackPosition(time)
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
    val trackArt: String,
    override val events: MusicPlayerUiEvent?
) : State<MusicPlayerUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<MusicPlayerUiEvent>> setEvent(event: MusicPlayerUiEvent?): S {
        return copy(events = event) as S
    }
}

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
            events = null
        )
    }
}

sealed class MusicPlayerUserAction : UserAction {
    object TogglePlay : MusicPlayerUserAction()
    object NextTapped : MusicPlayerUserAction()
    object PreviousTapped : MusicPlayerUserAction()
    data class ProgressTapped(val position: Int) : MusicPlayerUserAction()
}

sealed class MusicPlayerUiEvent : UiEvent
