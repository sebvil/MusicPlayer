package com.sebastianvm.musicplayer.ui.library.artists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.ArtistRepository
import com.sebastianvm.musicplayer.repository.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArtistsListViewModel @Inject constructor(
    initialState: ArtistsListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel<ArtistsListUserAction, ArtistsListUiEvent, ArtistsListState>(initialState) {

    init {
        collect(preferencesRepository.getArtistsListSortOrder()) { savedSortOrder ->
            setState {
                copy(
                    sortOrder = savedSortOrder,
                    artistsList = artistsList.sortedWith(getStringComparator(savedSortOrder) { item -> item.artistName }),
                )
            }
        }
        collect(artistRepository.getArtists()) { artists ->
            setState {
                copy(
                    artistsList = artists.map { artist ->
                        artist.toArtistRowState()
                    }
                        .sortedWith(getStringComparator(state.value.sortOrder) { item -> item.artistName }),
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
            ArtistsListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyArtistsListSortOrder(!state.value.sortOrder)
                }
            }
            ArtistsListUserAction.UpButtonClicked -> addUiEvent(ArtistsListUiEvent.NavigateUp)
        }
    }
}

data class ArtistsListState(
    val artistsList: List<ArtistRowState>,
    val sortOrder: SortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsListStateProvider(): ArtistsListState {
        return ArtistsListState(
            artistsList = listOf(),
            sortOrder = SortOrder.ASCENDING,
        )
    }
}

sealed class ArtistsListUserAction : UserAction {
    data class ArtistClicked(val artistGid: String) : ArtistsListUserAction()
    object SortByClicked : ArtistsListUserAction()
    object UpButtonClicked : ArtistsListUserAction()
}

sealed class ArtistsListUiEvent : UiEvent {
    data class NavigateToArtist(val artistGid: String) : ArtistsListUiEvent()
    object NavigateUp : ArtistsListUiEvent()
}
