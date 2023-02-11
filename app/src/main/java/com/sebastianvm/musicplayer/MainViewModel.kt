package com.sebastianvm.musicplayer

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.playback.PlaybackManager
import com.sebastianvm.musicplayer.ui.components.MediaArtImageState
import com.sebastianvm.musicplayer.ui.player.MinutesSecondsTime
import com.sebastianvm.musicplayer.ui.player.MusicPlayerViewState
import com.sebastianvm.musicplayer.ui.player.Percentage
import com.sebastianvm.musicplayer.ui.player.PlaybackControlsState
import com.sebastianvm.musicplayer.ui.player.PlaybackIcon
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
) : BaseViewModel<MainActivityState, MainActivityUserAction>(initialState) {

    init {
        playbackManager.playbackState.onEach {
            val newMusicPlayerViewState = if (it.mediaItemMetadata == null) null else {
                MusicPlayerViewState(
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
                    musicPlayerViewState = newMusicPlayerViewState
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: MainActivityUserAction) {
        when (action) {
            is MainActivityUserAction.ConnectToMusicService -> {
                playbackManager.connectToService()
            }

            is MainActivityUserAction.DisconnectFromMusicService -> {
                playbackManager.disconnectFromService()
            }
        }
    }
}


data class MainActivityState(val musicPlayerViewState: MusicPlayerViewState?) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialMainActivityStateModule {
    @Provides
    @ViewModelScoped
    fun initialMainActivityStateProvider(): MainActivityState = MainActivityState(
        musicPlayerViewState = null
    )
}

sealed class MainActivityUserAction : UserAction {
    object ConnectToMusicService : MainActivityUserAction()
    object DisconnectFromMusicService : MainActivityUserAction()
}

