package com.sebastianvm.musicplayer.ui.library.artists

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArtistsListViewModel @Inject constructor(
    initialState: ArtistsListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<ArtistsListUiEvent, ArtistsListState>(initialState) {

    init {
        viewModelScope.launch {
            preferencesRepository.getArtistsListSortOrder().flatMapLatest {
                setState {
                    copy(
                        sortOrder = it
                    )
                }
                artistRepository.getArtists(it)
            }.collect { artists ->
                setState {
                    copy(
                        artistsList = artists.map { artist ->
                            artist.toArtistRowState(shouldShowContextMenu = true)
                        }
                    )
                }
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
