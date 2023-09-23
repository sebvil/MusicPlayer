package com.sebastianvm.musicplayer.ui

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
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel(
    initialState: MainState,
    viewModelScope: CoroutineScope?,
    private val playbackManager: PlaybackManager
) : BaseViewModel<MainState, MainUserAction>(
    initialState = initialState,
    viewModelScope = viewModelScope
) {

    @Inject
    constructor(playbackManager: PlaybackManager) : this(
        initialState = MainState(playerViewState = null),
        viewModelScope = null,
        playbackManager = playbackManager
    )

    init {
        playbackManager.getPlaybackState().onEach { playbackState ->
            when (playbackState) {
                is TrackPlayingState -> {
                    val trackInfo = playbackState.trackInfo
                    setState {
                        it.copy(
                            playerViewState = PlayerViewState(
                                mediaArtImageState = MediaArtImageState(
                                    imageUri = trackInfo.artworkUri,
                                    backupImage = Icons.Album,
                                ),
                                trackInfoState = TrackInfoState(
                                    trackName = trackInfo.title,
                                    artists = trackInfo.artists
                                ),
                                trackProgressState = TrackProgressState(
                                    currentPlaybackTime = playbackState.currentTrackProgress,
                                    trackLength = trackInfo.trackLength
                                ),
                                playbackIcon = if (playbackState.isPlaying) PlaybackIcon.PAUSE else PlaybackIcon.PLAY
                            )
                        )
                    }
                }

                is NotPlayingState -> {
                    setState { it.copy(playerViewState = null) }
                }
            }
        }.launchIn(vmScope)
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
                playbackManager.togglePlay()
            }

            is MainUserAction.NextButtonClicked -> playbackManager.next()

            is MainUserAction.PreviousButtonClicked -> playbackManager.prev()

            is MainUserAction.ProgressBarClicked -> {
                val trackLengthMs =
                    state.playerViewState?.trackProgressState?.trackLength?.inWholeMilliseconds
                        ?: return
                val time: Long = (trackLengthMs * action.position / Percentage.MAX).toLong()
                playbackManager.seekToTrackPosition(time)
            }

            is MainUserAction.PlayMedia -> {
                playbackManager.playMedia(
                    mediaGroup = action.mediaGroup,
                    initialTrackIndex = action.initialTrackIndex
                ).launchIn(vmScope)
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
    data class ProgressBarClicked(val position: Int) : MainUserAction
    data class PlayMedia(val mediaGroup: MediaGroup, val initialTrackIndex: Int) : MainUserAction
}
