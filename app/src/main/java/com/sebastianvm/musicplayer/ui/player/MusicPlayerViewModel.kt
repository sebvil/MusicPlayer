package com.sebastianvm.musicplayer.ui.player

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.util.mvvm.DeprecatedBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
) : DeprecatedBaseViewModel<MusicPlayerState, MusicPlayerUserAction>(initialState) {

    private var isPlaying: Boolean = false
    private var trackLengthMs: Long = 0

    init {
        playbackManager.playbackState.onEach {
            val newPlayerViewState = if (it.mediaItemMetadata == null) {
                isPlaying = false
                trackLengthMs = 0
                null
            } else {
                isPlaying = it.isPlaying
                trackLengthMs = it.mediaItemMetadata.trackDurationMs
                PlayerViewState(
                    mediaArtImageState = MediaArtImageState(
                        imageUri = it.mediaItemMetadata.artworkUri,
                        contentDescription = R.string.album_art_for_album,
                        backupResource = R.drawable.ic_album,
                        backupContentDescription = R.string.placeholder_album_art,
                        args = listOf(it.mediaItemMetadata.title)
                    ),
                    trackInfoState = TrackInfoState(
                        trackName = it.mediaItemMetadata.title,
                        artists = it.mediaItemMetadata.artists
                    ),
                    playbackControlsState = PlaybackControlsState(
                        trackProgressState = TrackProgressState(
                            progress = Percentage(it.currentPlayTimeMs.toFloat() / it.mediaItemMetadata.trackDurationMs.toFloat()),
                            currentPlaybackTime = MinutesSecondsTime.fromMs(it.currentPlayTimeMs)
                                .toString(),
                            trackLength = MinutesSecondsTime.fromMs(it.mediaItemMetadata.trackDurationMs)
                                .toString()
                        ),
                        playbackIcon = if (it.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY
                    )
                )
            }
            setState {
                copy(
                    playerViewState = newPlayerViewState
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: MusicPlayerUserAction) {
        when (action) {
            is MusicPlayerUserAction.PlayToggled -> {
                if (isPlaying) {
                    playbackManager.pause()
                } else {
                    playbackManager.play()
                }
            }

            is MusicPlayerUserAction.NextButtonClicked -> playbackManager.next()

            is MusicPlayerUserAction.PreviousButtonClicked -> playbackManager.prev()

            is MusicPlayerUserAction.ProgressBarClicked -> {
                val time: Long = trackLengthMs * action.position / 100
                playbackManager.seekToTrackPosition(time)
            }
        }
    }
}

data class MusicPlayerState(
    val playerViewState: PlayerViewState?
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialMusicPlayerStateModule {
    @Provides
    @ViewModelScoped
    fun initialMusicPlayerStateProvider(): MusicPlayerState {
        return MusicPlayerState(playerViewState = null)
    }
}

sealed interface MusicPlayerUserAction : UserAction {
    object PlayToggled : MusicPlayerUserAction
    object NextButtonClicked : MusicPlayerUserAction
    object PreviousButtonClicked : MusicPlayerUserAction
    data class ProgressBarClicked(val position: Int) : MusicPlayerUserAction
}