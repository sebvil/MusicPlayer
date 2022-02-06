package com.sebastianvm.musicplayer.ui.library.artists

import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.launchViewModelIOScope
import com.sebastianvm.musicplayer.ui.util.mvvm.state.State
import com.sebastianvm.musicplayer.util.SortOrder
import com.sebastianvm.musicplayer.util.getStringComparator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


@HiltViewModel
class ArtistsListViewModel @Inject constructor(
    initialState: ArtistsListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel<ArtistsListUserAction, ArtistsListUiEvent, ArtistsListState>(initialState) {

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


    override fun handle(action: ArtistsListUserAction) {
        when (action) {
            is ArtistsListUserAction.ArtistClicked -> {
                addUiEvent(
                    ArtistsListUiEvent.NavigateToArtist(action.artistName)
                )
            }
            is ArtistsListUserAction.SortByClicked -> {
                launchViewModelIOScope {
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
