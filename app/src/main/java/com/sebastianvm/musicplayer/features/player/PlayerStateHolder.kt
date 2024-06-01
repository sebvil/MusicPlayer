package com.sebastianvm.musicplayer.features.player

import com.sebastianvm.musicplayer.designsystem.icons.Album
import com.sebastianvm.musicplayer.designsystem.icons.Icons
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.queue.QueueUiComponent
import com.sebastianvm.musicplayer.repository.playback.NotPlayingState
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration

sealed interface PlayerState : State {
    data class Playing(
        val mediaArtImageState: MediaArtImageState,
        val trackInfoState: TrackInfoState,
        val trackProgressState: TrackProgressState,
        val playbackIcon: PlaybackIcon,
        val isFullscreen: Boolean,
        val queueUiComponent: QueueUiComponent?
    ) : PlayerState

    data object NotPlaying : PlayerState
}

sealed interface PlayerUserAction : UserAction {
    data object PlayToggled : PlayerUserAction
    data object NextButtonClicked : PlayerUserAction
    data object PreviousButtonClicked : PlayerUserAction
    data class ProgressBarClicked(val position: Int, val trackLength: Duration) : PlayerUserAction
    data object DismissFullScreenPlayer : PlayerUserAction
    data object QueueTapped : PlayerUserAction
    data object DismissQueue : PlayerUserAction
}

data class PlayerProps(val isFullscreen: Boolean)

interface PlayerDelegate {
    fun dismissFullScreenPlayer()
}

class PlayerStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playbackManager: PlaybackManager,
    private val delegate: PlayerDelegate,
    props: Flow<PlayerProps>,
) : StateHolder<PlayerState, PlayerUserAction> {

    private val queueUiComponent = QueueUiComponent

    private val showQueue: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<PlayerState> =
        combine(
            playbackManager.getPlaybackState(),
            props,
            showQueue
        ) { playbackState, props, showQueue ->
            when (playbackState) {
                is TrackPlayingState -> {
                    PlayerState.Playing(
                        mediaArtImageState = MediaArtImageState(
                            imageUri = playbackState.trackInfo.artworkUri,
                            backupImage = Icons.Album
                        ),
                        trackInfoState = TrackInfoState(
                            trackName = playbackState.trackInfo.title,
                            artists = playbackState.trackInfo.artists,
                        ),
                        trackProgressState = TrackProgressState(
                            currentPlaybackTime = playbackState.currentTrackProgress,
                            trackLength = playbackState.trackInfo.trackLength
                        ),
                        playbackIcon = if (playbackState.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY,
                        isFullscreen = props.isFullscreen,
                        queueUiComponent = if (showQueue) queueUiComponent else null
                    )
                }

                is NotPlayingState -> PlayerState.NotPlaying
            }
        }.stateIn(
            scope = stateHolderScope,
            started = SharingStarted.Lazily,
            initialValue = PlayerState.NotPlaying
        )

    override fun handle(action: PlayerUserAction) {
        when (action) {
            is PlayerUserAction.PlayToggled -> {
                playbackManager.togglePlay()
            }

            is PlayerUserAction.NextButtonClicked -> playbackManager.next()

            is PlayerUserAction.PreviousButtonClicked -> playbackManager.prev()

            is PlayerUserAction.ProgressBarClicked -> {
                val trackLengthMs = action.trackLength.inWholeMilliseconds
                val time: Long = (trackLengthMs * action.position / Percentage.MAX).toLong()
                playbackManager.seekToTrackPosition(time)
            }

            is PlayerUserAction.DismissFullScreenPlayer -> {
                delegate.dismissFullScreenPlayer()
            }

            is PlayerUserAction.QueueTapped -> {
                showQueue.update { true }
            }

            is PlayerUserAction.DismissQueue -> {
                showQueue.update { false }
            }
        }
    }
}

fun getPlayerStateHolder(
    dependencies: DependencyContainer,
    delegate: PlayerDelegate,
    props: Flow<PlayerProps>,
): PlayerStateHolder {
    return PlayerStateHolder(
        playbackManager = dependencies.repositoryProvider.playbackManager,
        delegate = delegate,
        props = props,
    )
}
