package com.sebastianvm.musicplayer.ui.player

import com.sebastianvm.musicplayer.repository.playback.MediaPlaybackRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
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
                    trackName = it.mediaItemMetadata?.title,
                    artists = it.mediaItemMetadata?.artists,
                    trackArt = it.mediaItemMetadata?.artworkUri ?: "",
                    trackLengthMs = it.mediaItemMetadata?.trackDurationMs,
                    isPlaying = it.isPlaying,
                    currentPlaybackTimeMs = it.currentPlayTimeMs,
                )
            }
        }
    }

    fun onPlayToggled() {
        if (state.value.isPlaying) {
            mediaPlaybackRepository.pause()
        } else {
            mediaPlaybackRepository.play()
        }
    }

    fun onPreviousTapped() {
        mediaPlaybackRepository.prev()
    }

    fun onNextTapped() {
        mediaPlaybackRepository.next()
    }

    fun onProgressTapped(position: Int) {
        val time: Long = (state.value.trackLengthMs ?: 0) * position / 100
        mediaPlaybackRepository.seekToTrackPosition(time)
    }
}

data class MusicPlayerState(
    val isPlaying: Boolean,
    val trackName: String?,
    val artists: String?,
    val trackLengthMs: Long?,
    val currentPlaybackTimeMs: Long?,
    val trackArt: String,
    override val events: List<MusicPlayerUiEvent>
) : State<MusicPlayerUiEvent> {

    @Suppress("UNCHECKED_CAST")
    override fun <S : State<MusicPlayerUiEvent>> setEvent(events: List<MusicPlayerUiEvent>): S {
        return copy(events = events) as S
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
            events = listOf()
        )
    }
}

sealed class MusicPlayerUiEvent : UiEvent
