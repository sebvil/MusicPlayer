package com.sebastianvm.musicplayer.features.artistsmenu

import com.sebastianvm.musicplayer.features.artist.screen.ArtistArguments
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.Data
import com.sebastianvm.musicplayer.ui.util.mvvm.Delegate
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

data class ArtistsMenuArguments(val mediaType: MediaWithArtists, val mediaId: Long) : Arguments
interface ArtistsMenuDelegate : Delegate {
    fun showArtist(arguments: ArtistArguments)
}

data class ArtistsMenuState(val modelListState: ModelListState) : State

sealed interface ArtistsMenuUserAction : UserAction {
    data class ArtistClicked(val artistId: Long) : ArtistsMenuUserAction
}

class ArtistsMenuStateHolder(
    stateHolderScope: CoroutineScope = stateHolderScope(),
    arguments: ArtistsMenuArguments,
    artistRepository: ArtistRepository,
    private val delegate: ArtistsMenuDelegate,
) : StateHolder<UiState<ArtistsMenuState>, ArtistsMenuUserAction> {

    override val state: StateFlow<UiState<ArtistsMenuState>> =
        artistRepository.getArtistsForMedia(arguments.mediaType, arguments.mediaId)
            .map { artists ->
                Data(
                    ArtistsMenuState(
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

    override fun handle(action: ArtistsMenuUserAction) {
        when (action) {
            is ArtistsMenuUserAction.ArtistClicked -> {
                delegate.showArtist(ArtistArguments(artistId = action.artistId))
            }
        }
    }
}

