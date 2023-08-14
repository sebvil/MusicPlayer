package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.DeprecatedBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
    arguments: ArtistsMenuArguments,
    initialState: ArtistsBottomSheetState,
    artistRepository: ArtistRepository,
) :
    DeprecatedBaseViewModel<ArtistsBottomSheetState, ArtistsBottomSheetUserAction>(
        initialState
    ) {
    init {
        artistRepository.getArtists(arguments.mediaType, arguments.mediaId).onEach { artists ->
            setState {
                copy(
                    artistList = artists.map { it.toModelListItemState(trailingButtonType = null) }
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: ArtistsBottomSheetUserAction) {
        when (action) {
            is ArtistsBottomSheetUserAction.ArtistRowClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        ArtistRouteDestination(
                            ArtistArguments(artistId = action.artistId)
                        )
                    )
                )
            }
        }
    }
}

data class ArtistsMenuArguments(val mediaType: MediaWithArtists, val mediaId: Long)

data class ArtistsBottomSheetState(val artistList: List<ModelListItemState>) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsBottomSheetStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsBottomSheetStateProvider(): ArtistsBottomSheetState {
        return ArtistsBottomSheetState(
            artistList = listOf(),
        )
    }

    @Provides
    @ViewModelScoped
    fun artistMenuArgumentProvider(savedStateHandle: SavedStateHandle): ArtistsMenuArguments {
        return savedStateHandle.navArgs()
    }
}

sealed interface ArtistsBottomSheetUserAction : UserAction {
    data class ArtistRowClicked(val artistId: Long) : ArtistsBottomSheetUserAction
}
