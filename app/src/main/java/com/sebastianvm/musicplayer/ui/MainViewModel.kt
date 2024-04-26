package com.sebastianvm.musicplayer.ui

import androidx.lifecycle.ViewModel
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.repository.playback.NotPlayingState
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.repository.playback.TrackPlayingState
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.player.Percentage
import com.sebastianvm.musicplayer.ui.player.PlaybackIcon
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.player.TrackInfoState
import com.sebastianvm.musicplayer.ui.player.TrackProgressState
import com.sebastianvm.musicplayer.ui.util.CloseableCoroutineScope
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class MainViewModel(
    private val stateHolderScope: CloseableCoroutineScope = stateHolderScope(),
    private val playbackManager: PlaybackManager
) : StateHolder<MainState, MainUserAction>, ViewModel(stateHolderScope) {

    override val state: StateFlow<MainState> =
        playbackManager.getPlaybackState().map { playbackState ->
            MainState(
                playerViewState = when (playbackState) {
                    is TrackPlayingState -> {
                        PlayerViewState(
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
                        )
                    }

                    is NotPlayingState -> null
                }
            )
        }.stateIn(stateHolderScope, SharingStarted.Eagerly, MainState(playerViewState = null))

    override fun handle(action: MainUserAction) {
        when (action) {
            is MainUserAction.ConnectToMusicService -> {
                playbackManager.connectToService()
            }

            is MainUserAction.DisconnectFromMusicService -> {
                playbackManager.disconnectFromService()
            }

            is MainUserAction.PlayToggled -> {
                playbackManager.togglePlay()
            }

            is MainUserAction.NextButtonClicked -> playbackManager.next()

            is MainUserAction.PreviousButtonClicked -> playbackManager.prev()

            is MainUserAction.ProgressBarClicked -> {
                val trackLengthMs = action.trackLength.inWholeMilliseconds
                val time: Long = (trackLengthMs * action.position / Percentage.MAX).toLong()
                playbackManager.seekToTrackPosition(time)
            }

            is MainUserAction.PlayMedia -> {
                playbackManager.playMedia(
                    mediaGroup = action.mediaGroup,
                    initialTrackIndex = action.initialTrackIndex
                ).launchIn(stateHolderScope)
            }
        }
    }
}

data class MainState(val playerViewState: PlayerViewState?) : State

sealed interface MainUserAction : UserAction {
    data object ConnectToMusicService : MainUserAction
    data object DisconnectFromMusicService : MainUserAction
    data object PlayToggled : MainUserAction
    data object NextButtonClicked : MainUserAction
    data object PreviousButtonClicked : MainUserAction
    data class ProgressBarClicked(val position: Int, val trackLength: Duration) : MainUserAction
    data class PlayMedia(val mediaGroup: MediaGroup, val initialTrackIndex: Int) : MainUserAction
}
