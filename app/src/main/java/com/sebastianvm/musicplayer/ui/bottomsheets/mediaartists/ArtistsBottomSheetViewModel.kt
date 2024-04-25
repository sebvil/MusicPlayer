package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.artist.ArtistArguments
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.destinations.ArtistRouteDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.OldBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ArtistsBottomSheetViewModel(
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository
) : OldBaseViewModel<ArtistsBottomSheetState, ArtistsBottomSheetUserAction>() {
    init {
        artistRepository.getArtistsForMedia(arguments.mediaType, arguments.mediaId)
            .onEach { artists ->
                setDataState {
                    it.copy(
                        modelListState = ModelListState(
                            items = artists.map { artist ->
                                artist.toModelListItemState(
                                    trailingButtonType = null
                                )
                            }
                        )
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

    override val defaultState: ArtistsBottomSheetState by lazy {
        ArtistsBottomSheetState(modelListState = ModelListState())
    }
}

data class ArtistsMenuArguments(val mediaType: MediaWithArtists, val mediaId: Long)

data class ArtistsBottomSheetState(val modelListState: ModelListState) : State

sealed interface ArtistsBottomSheetUserAction : UserAction {
    data class ArtistRowClicked(val artistId: Long) : ArtistsBottomSheetUserAction
}
