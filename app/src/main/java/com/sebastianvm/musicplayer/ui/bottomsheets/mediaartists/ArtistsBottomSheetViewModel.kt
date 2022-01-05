package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.AlbumRepository
import com.sebastianvm.musicplayer.repository.TrackRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject


@HiltViewModel
class ArtistsBottomSheetViewModel @Inject constructor(
    initialState: ArtistsBottomSheetState,
    trackRepository: TrackRepository,
    albumRepository: AlbumRepository
) :
    BaseViewModel<ArtistsBottomSheetUserAction, ArtistsBottomSheetUiEvent, ArtistsBottomSheetState>(
        initialState
    ) {

    init {
        with(state.value.mediaGroup) {
            when (mediaType) {
                MediaType.ALL_TRACKS -> {
                    collect(trackRepository.getTrack(mediaId)) { track ->
                        setState {
                            copy(
                                artistsList = track.artists.map { it.toArtistRowState() }
                            )
                        }
                    }
                }
                MediaType.ALBUM -> {
                    collect(albumRepository.getAlbum(mediaId)) { album ->
                        setState {
                            copy(
                                artistsList = album.artists.map { it.toArtistRowState() }
                            )
                        }
                    }
                }
                else -> Unit
            }
        }

    }

    override fun handle(action: ArtistsBottomSheetUserAction) {
        when (action) {
            is ArtistsBottomSheetUserAction.ArtistClicked -> {
                addUiEvent(ArtistsBottomSheetUiEvent.NavigateToArtist(action.artistId))
            }
        }
    }
}

data class ArtistsBottomSheetState(
    val mediaGroup: MediaGroup,
    val artistsList: List<ArtistRowState>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsBottomSheetStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsBottomSheetStateProvider(savedStateHandle: SavedStateHandle): ArtistsBottomSheetState {
        val mediaType = MediaType.valueOf(savedStateHandle.get<String>(NavArgs.MEDIA_TYPE)!!)
        val mediaId = savedStateHandle.get<String>(NavArgs.MEDIA_ID)!!
        return ArtistsBottomSheetState(
            mediaGroup = MediaGroup(mediaType = mediaType, mediaId = mediaId),
            artistsList = listOf()
        )
    }
}

sealed class ArtistsBottomSheetUserAction : UserAction {
    data class ArtistClicked(val artistId: String) : ArtistsBottomSheetUserAction()
}

sealed class ArtistsBottomSheetUiEvent : UiEvent {
    data class NavigateToArtist(val artistId: String) : ArtistsBottomSheetUiEvent()
}

