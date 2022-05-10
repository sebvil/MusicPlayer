package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
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
    artistRepository: ArtistRepository,
) :
    BaseViewModel<ArtistsBottomSheetUiEvent, ArtistsBottomSheetState>(initialState) {
    init {
        with(state.value) {
            when (mediaType) {
                MediaType.TRACK -> {
                    collect(artistRepository.getArtistsForTrack(mediaId)) { artists ->
                        setState {
                            copy(
                                artistsList = artists.map { it.toArtistRowState() }
                            )
                        }
                    }
                }
                MediaType.ALBUM -> {
                    collect(artistRepository.getArtistsForAlbum(mediaId.toLong())) { artists ->
                        setState {
                            copy(
                                artistsList = artists.map { it.toArtistRowState() }
                            )
                        }
                    }
                }
                else -> throw IllegalStateException("Media artists bottom sheet not supported for mediaType: $mediaType")
            }
        }

    }

    fun <A: UserAction> handle(action: A) {
        when (action) {
            is ArtistsBottomSheetUserAction.ArtistClicked -> {
                addUiEvent(ArtistsBottomSheetUiEvent.NavigateToArtist(action.artistName))
            }
        }
    }
}

data class ArtistsBottomSheetState(
    val mediaType: MediaType,
    val mediaId: String,
    val artistsList: List<ArtistRowState>,
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
            mediaId = mediaId,
            mediaType = mediaType,
            artistsList = listOf(),
        )
    }
}

sealed class ArtistsBottomSheetUserAction : UserAction {
    data class ArtistClicked(val artistName: String) : ArtistsBottomSheetUserAction()
}

sealed class ArtistsBottomSheetUiEvent : UiEvent {
    data class NavigateToArtist(val artistName: String) : ArtistsBottomSheetUiEvent()
}
