package com.sebastianvm.musicplayer.ui.library.artists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepositoryImpl
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
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
    private val preferencesRepository: SortPreferencesRepositoryImpl,
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

    fun onArtistClicked(artistName: String) {
        addUiEvent(ArtistsListUiEvent.NavigateToArtist(artistName))
    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyArtistsListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addUiEvent(ArtistsListUiEvent.NavigateUp)
    }

    fun onArtistOverflowMenuIconClicked(artistName: String) {
        addUiEvent(ArtistsListUiEvent.OpenContextMenu(artistName))
    }
}

data class ArtistsListState(
    val artistsList: List<ArtistRowState>,
    val sortOrder: MediaSortOrder,
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

sealed class ArtistsListUiEvent : UiEvent {
    data class NavigateToArtist(val artistName: String) : ArtistsListUiEvent()
    object NavigateUp : ArtistsListUiEvent()
    data class OpenContextMenu(val artistName: String) : ArtistsListUiEvent()
}
