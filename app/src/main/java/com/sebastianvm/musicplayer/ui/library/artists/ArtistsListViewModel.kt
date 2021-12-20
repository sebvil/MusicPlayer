package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.repository.ArtistRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
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
class ArtistsListViewModel @Inject constructor(
    initialState: ArtistsListState,
    artistRepository: ArtistRepository,
) : BaseViewModel<ArtistsListUserAction, ArtistsListUiEvent, ArtistsListState>(initialState) {

    init {
        collect(artistRepository.getArtists()) { artists ->
            setState {
                copy(
                    artistsList = artists.map { artist ->
                        artist.toArtistRowState()
                    }.sortedBy { item -> item.artistName },
                )
            }
        }
    }


    override fun handle(action: ArtistsListUserAction) {
        when (action) {
            is ArtistsListUserAction.ArtistClicked -> {
                addUiEvent(
                    ArtistsListUiEvent.NavigateToArtist(action.artistGid)
                )
            }
        }
    }
}

data class ArtistsListState(
    val artistsList: List<ArtistRowState>
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsListStateProvider(): ArtistsListState {
        return ArtistsListState(
            artistsList = listOf()
        )
    }
}

sealed class ArtistsListUserAction : UserAction {
    data class ArtistClicked(val artistGid: String) : ArtistsListUserAction()
}

sealed class ArtistsListUiEvent : UiEvent {
    data class NavigateToArtist(val artistGid: String) : ArtistsListUiEvent()
}
