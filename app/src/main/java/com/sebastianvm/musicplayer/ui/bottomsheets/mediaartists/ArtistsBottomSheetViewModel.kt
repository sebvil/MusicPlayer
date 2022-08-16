package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import com.sebastianvm.musicplayer.player.MediaType
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
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
                                artistList = artists.map { it.toModelListItemState() }
                            )
                        }
                    }
                }
                MediaType.ALBUM -> {
                    collect(artistRepository.getArtistsForAlbum(mediaId)) { artists ->
                        setState {
                            copy(
                                artistList = artists.map { it.toModelListItemState() }
                            )
                        }
                    }
                }
                else -> throw IllegalStateException("Media artists bottom sheet not supported for mediaType: $mediaType")
            }
        }

    }

    fun onArtistClicked(artistId: Long) {
        addUiEvent(ArtistsBottomSheetUiEvent.NavigateToArtist(artistId = artistId))
    }

}

data class ArtistsBottomSheetState(
    val mediaType: MediaType,
    val mediaId: Long,
    val artistList: List<ModelListItemState>,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsBottomSheetStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsBottomSheetStateProvider(savedStateHandle: SavedStateHandle): ArtistsBottomSheetState {
        val args = savedStateHandle.getArgs<ArtistsMenuArguments>()
        return ArtistsBottomSheetState(
            mediaId = args.mediaId,
            mediaType = args.mediaType,
            artistList = listOf(),
        )
    }
}

sealed class ArtistsBottomSheetUiEvent : UiEvent {
    data class NavigateToArtist(val artistId: Long) : ArtistsBottomSheetUiEvent()
}
