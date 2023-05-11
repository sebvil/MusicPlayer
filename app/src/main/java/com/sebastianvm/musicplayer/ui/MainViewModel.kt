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
class MainViewModel @Inject constructor(
    initialState: MainActivityState,
    private val playbackManager: PlaybackManager
) : BaseViewModel<MainActivityState, MainUserAction>(initialState) {

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

@InstallIn(ViewModelComponent::class)
@Module
object InitialMainActivityStateModule {
    @Provides
    @ViewModelScoped
    fun initialMainActivityStateProvider(): MainActivityState = MainActivityState(
        playerViewState = null
    )
}

sealed interface MainUserAction : UserAction {
    object ConnectToMusicService : MainUserAction
    object DisconnectFromMusicService : MainUserAction
    object PlayToggled : MainUserAction
    object NextButtonClicked : MainUserAction
    object PreviousButtonClicked : MainUserAction
    data class ProgressBarClicked(val position: Int) : MainUserAction
}

