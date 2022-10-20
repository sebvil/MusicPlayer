package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.extensions.getArgs
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
class ArtistsBottomSheetViewModel @Inject constructor(
    initialState: ArtistsBottomSheetState,
    artistRepository: ArtistRepository,
) :
    BaseViewModel<ArtistsBottomSheetState, ArtistsBottomSheetUserAction, ArtistsBottomSheetUiEvent>(
        initialState
    ) {
    init {
        artistRepository.getArtists(state.artistIds).onEach { artists ->
            setState {
                copy(
                    artistList = artists.map { it.toModelListItemState() }
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: ArtistsBottomSheetUserAction) {
        TODO("handle Not yet implemented")
    }

    fun onArtistClicked(artistId: Long) {
        addUiEvent(ArtistsBottomSheetUiEvent.NavigateToArtist(artistId = artistId))
    }

}

data class ArtistsBottomSheetState(
    val artistIds: List<Long>,
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
            artistIds = args.artistIds,
            artistList = listOf(),
        )
    }
}

sealed interface ArtistsBottomSheetUiEvent : UiEvent {
    data class NavigateToArtist(val artistId: Long) : ArtistsBottomSheetUiEvent
}

sealed interface ArtistsBottomSheetUserAction : UserAction
