package com.sebastianvm.musicplayer.ui.bottomsheets.mediaartists

import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Loading
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UiState
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArtistsBottomSheetStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository
) : StateHolder<UiState<ArtistsBottomSheetState>, ArtistsBottomSheetUserAction> {

    override val state: StateFlow<UiState<ArtistsBottomSheetState>> =
        artistRepository.getArtistsForMedia(arguments.mediaType, arguments.mediaId)
            .map { artists ->
                Data(
                    ArtistsBottomSheetState(
                        modelListState = ModelListState(
                            items = artists.map { artist ->
                                artist.toModelListItemState(
                                    trailingButtonType = null
                                )
                            }
                        )
                    )
                )
            }.stateIn(stateHolderScope, Lazily, Loading)

    override fun handle(action: ArtistsBottomSheetUserAction) = Unit
}

data class ArtistsMenuArguments(val mediaType: MediaWithArtists, val mediaId: Long)

data class ArtistsBottomSheetState(val modelListState: ModelListState) : State

sealed interface ArtistsBottomSheetUserAction : UserAction
