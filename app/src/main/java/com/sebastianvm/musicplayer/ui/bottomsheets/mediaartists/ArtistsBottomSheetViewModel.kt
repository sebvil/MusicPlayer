package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
        when (action) {
            is ArtistsBottomSheetUserAction.ArtistRowClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Artist(
                            ArtistArguments(artistId = action.artistId)
                        )
                    )
                )
            }
        }
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

sealed interface ArtistsBottomSheetUserAction : UserAction {
    data class ArtistRowClicked(val artistId: Long) : ArtistsBottomSheetUserAction
}

sealed interface ArtistsBottomSheetUiEvent : UiEvent