package com.sebastianvm.musicplayer.features.player

import com.sebastianvm.musicplayer.core.model.NotPlayingState
import com.sebastianvm.musicplayer.core.model.TrackPlayingState
import com.sebastianvm.musicplayer.core.services.playback.PlaybackManager
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
import com.sebastianvm.musicplayer.features.api.player.PlayerDelegate
import com.sebastianvm.musicplayer.features.api.player.PlayerProps
import com.sebastianvm.musicplayer.features.api.queue.queue
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

sealed interface PlayerState : State {

    sealed interface Playing : PlayerState

    data class FloatingState(
        val artworkUri: String,
        val trackInfoState: TrackInfoState,
        val trackProgressState: TrackProgressState,
        val playbackIcon: PlaybackIcon,
    ) : Playing

    data class FullScreenState(
        val artworkUri: String,
        val trackInfoState: TrackInfoState,
        val trackProgressState: TrackProgressState,
        val playbackIcon: PlaybackIcon,
    ) : Playing

    data class QueueState(
        val trackInfoState: TrackInfoState,
        val trackProgressState: TrackProgressState,
        val playbackIcon: PlaybackIcon,
        val queueUiComponent: UiComponent<*>,
    ) : Playing

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

class PlayerStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    private val playbackManager: PlaybackManager,
    private val delegate: PlayerDelegate,
    props: Flow<PlayerProps>,
    features: FeatureRegistry,
) : StateHolder<PlayerState, PlayerUserAction> {

    private val queueUiComponent = features.queue().queueUiComponent()

    private val showQueue: MutableStateFlow<Boolean> = MutableStateFlow(false)

    override val state: StateFlow<PlayerState> =
        combine(playbackManager.getPlaybackState(), props, showQueue) {
                playbackState,
                props,
                showQueue ->
                when (playbackState) {
                    is TrackPlayingState -> {
                        val artworkUri = playbackState.trackInfo.artworkUri
                        val trackInfoState =
                            TrackInfoState(
                                trackName = playbackState.trackInfo.title,
                                artists = playbackState.trackInfo.artists,
                            )
                        val trackProgressState =
                            TrackProgressState(
                                currentPlaybackTime = playbackState.currentTrackProgress,
                                trackLength = playbackState.trackInfo.trackLength,
                            )
                        val playbackIcon =
                            if (playbackState.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY
                        when {
                            props.isFullscreen && showQueue ->
                                PlayerState.QueueState(
                                    trackInfoState = trackInfoState,
                                    trackProgressState = trackProgressState,
                                    playbackIcon = playbackIcon,
                                    queueUiComponent = queueUiComponent,
                                )
                            props.isFullscreen ->
                                PlayerState.FullScreenState(
                                    artworkUri = artworkUri,
                                    trackInfoState = trackInfoState,
                                    trackProgressState = trackProgressState,
                                    playbackIcon = playbackIcon,
                                )
                            else ->
                                PlayerState.FloatingState(
                                    artworkUri = artworkUri,
                                    trackInfoState = trackInfoState,
                                    trackProgressState = trackProgressState,
                                    playbackIcon = playbackIcon,
                                )
                        }
                    }
                    is NotPlayingState -> {
                        PlayerState.NotPlaying
                    }
                }
            }
            .stateIn(
                scope = stateHolderScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlayerState.NotPlaying,
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
