package com.sebastianvm.musicplayer.ui.library.artists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArtistsListViewModel @Inject constructor(
    initialState: ArtistsListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel<ArtistsListUiEvent, ArtistsListState>(initialState) {

    init {
        collect(
            preferencesRepository.getArtistsListSortOrder()
                .combine(artistRepository.getArtists()) { sortOrder, artists ->
                    Pair(sortOrder, artists)
                }) { (savedSortOrder, artists) ->
            setState {
                copy(
                    sortOrder = savedSortOrder,
                    artistsList = artists.map { artist ->
                        artist.toArtistRowState(shouldShowContextMenu = true)
                    }.sortedWith(getStringComparator(savedSortOrder) { item -> item.artistName }),
                )
            }
        }
    }

    fun artistClicked() {}


    fun <A : UserAction> handle(action: A) {
        when (action) {
            is ArtistsListUserAction.ArtistClicked -> {
                addUiEvent(
                    ArtistsListUiEvent.NavigateToArtist(action.artistName)
                )
            }
            is ArtistsListUserAction.SortByClicked -> {
                viewModelScope.launch {
                    preferencesRepository.modifyArtistsListSortOrder(!state.value.sortOrder)
                }
            }
            is ArtistsListUserAction.UpButtonClicked -> addUiEvent(ArtistsListUiEvent.NavigateUp)
            is ArtistsListUserAction.ContextMenuIconClicked -> {
                addUiEvent(ArtistsListUiEvent.OpenContextMenu(action.artistName))
            }
        }
    }
}

data class ArtistsListState(
    val artistsList: List<ArtistRowState>,
    val sortOrder: MediaSortOrder
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistsListStateProvider(): ArtistsListState {
        return ArtistsListState(
            artistsList = listOf(),
            sortOrder = MediaSortOrder.ASCENDING,
        )
    }
}

sealed class ArtistsListUserAction : UserAction {
    data class ArtistClicked(val artistName: String) : ArtistsListUserAction()
    object SortByClicked : ArtistsListUserAction()
    object UpButtonClicked : ArtistsListUserAction()
    data class ContextMenuIconClicked(val artistName: String) : ArtistsListUserAction()
}

sealed class ArtistsListUiEvent : UiEvent {
    data class NavigateToArtist(val artistName: String) : ArtistsListUiEvent()
    object NavigateUp : ArtistsListUiEvent()
    data class OpenContextMenu(val artistName: String) : ArtistsListUiEvent()
}
