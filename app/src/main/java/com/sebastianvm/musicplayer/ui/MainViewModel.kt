package com.sebastianvm.musicplayer.ui

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.player.MinutesSecondsTime
import com.sebastianvm.musicplayer.ui.player.Percentage
import com.sebastianvm.musicplayer.ui.player.PlaybackControlsState
import com.sebastianvm.musicplayer.ui.player.PlaybackIcon
import com.sebastianvm.musicplayer.ui.player.PlayerViewState
import com.sebastianvm.musicplayer.ui.player.TrackInfoState
import com.sebastianvm.musicplayer.ui.player.TrackProgressState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playbackManager: PlaybackManager
) : BaseViewModel<MainActivityState, MainUserAction>() {

    private var isPlaying: Boolean = false
    private var trackLengthMs: Long = 0

    override val defaultState: MainActivityState by lazy {
        MainActivityState(playerViewState = null)
    }
    
    init {
        playbackManager.playbackState.onEach { playbackState ->
            val mediaItemMetadata = playbackState.mediaItemMetadata
            val newPlayerViewState = if (mediaItemMetadata == null) {
                isPlaying = false
                trackLengthMs = 0
                null
            } else {
                isPlaying = playbackState.isPlaying
                trackLengthMs = mediaItemMetadata.trackDurationMs
                PlayerViewState(
                    mediaArtImageState = MediaArtImageState(
                        imageUri = mediaItemMetadata.artworkUri,
                        contentDescription = R.string.album_art_for_album,
                        backupResource = R.drawable.ic_album,
                        backupContentDescription = R.string.placeholder_album_art,
                        args = listOf(mediaItemMetadata.title)
                    ),
                    trackInfoState = TrackInfoState(
                        trackName = mediaItemMetadata.title,
                        artists = mediaItemMetadata.artists
                    ),
                    playbackControlsState = PlaybackControlsState(
                        trackProgressState = TrackProgressState(
                            progress = Percentage(playbackState.currentPlayTimeMs.toFloat() / mediaItemMetadata.trackDurationMs.toFloat()),
                            currentPlaybackTime = MinutesSecondsTime.fromMs(playbackState.currentPlayTimeMs)
                                .toString(),
                            trackLength = MinutesSecondsTime.fromMs(mediaItemMetadata.trackDurationMs)
                                .toString()
                        ),
                        playbackIcon = if (playbackState.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY
                    )
                )
            }
            setDataState {
                it.copy(
                    playerViewState = newPlayerViewState
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: MainUserAction) {
        when (action) {
            is MainUserAction.ConnectToMusicService -> {
                playbackManager.connectToService()
            }

            is MainUserAction.DisconnectFromMusicService -> {
                playbackManager.disconnectFromService()
            }

            is MainUserAction.PlayToggled -> {
                if (isPlaying) {
                    playbackManager.pause()
                } else {
                    playbackManager.play()
                }
            }

            is MainUserAction.NextButtonClicked -> playbackManager.next()

            is MainUserAction.PreviousButtonClicked -> playbackManager.prev()

            is MainUserAction.ProgressBarClicked -> {
                val time: Long = trackLengthMs * action.position / 100
                playbackManager.seekToTrackPosition(time)
            }
        }
    }


}


data class MainActivityState(val playerViewState: PlayerViewState?) : State

sealed interface MainUserAction : UserAction {
    data object ConnectToMusicService : MainUserAction
    data object DisconnectFromMusicService : MainUserAction
    data object PlayToggled : MainUserAction
    data object NextButtonClicked : MainUserAction
    data object PreviousButtonClicked : MainUserAction
    data class ProgressBarClicked(val position: Int) : MainUserAction
}

