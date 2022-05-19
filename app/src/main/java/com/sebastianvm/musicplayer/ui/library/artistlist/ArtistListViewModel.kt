package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.ArtistRowState
import com.sebastianvm.musicplayer.ui.components.toArtistRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
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
class ArtistListViewModel @Inject constructor(
    initialState: ArtistListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<ArtistListUiEvent, ArtistListState>(initialState) {

    init {
        viewModelScope.launch {
            preferencesRepository.getArtistListSortOrder().flatMapLatest {
                setState {
                    copy(
                        sortOrder = it
                    )
                }
                artistRepository.getArtists(it)
            }.collect { artists ->
                setState {
                    copy(
                        artistList = artists.map { artist ->
                            artist.toArtistRowState(shouldShowContextMenu = true)
                        }
                    )
                }
            }
        }

    }

    fun onArtistClicked(artistId: Long) {
        addUiEvent(ArtistListUiEvent.NavigateToArtist(artistId))
    }

    fun onSortByClicked() {
        viewModelScope.launch {
            preferencesRepository.modifyArtistListSortOrder(!state.value.sortOrder)
        }
    }

    fun onUpButtonClicked() {
        addUiEvent(ArtistListUiEvent.NavigateUp)
    }

    fun onArtistOverflowMenuIconClicked(artistId: Long) {
        addUiEvent(ArtistListUiEvent.OpenContextMenu(artistId))
    }
}

data class ArtistListState(
    val artistList: List<ArtistRowState>,
    val sortOrder: MediaSortOrder,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistListStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistListStateProvider(): ArtistListState {
        return ArtistListState(
            artistList = listOf(),
            sortOrder = MediaSortOrder.ASCENDING,
        )
    }
}

sealed class ArtistListUiEvent : UiEvent {
    data class NavigateToArtist(val artistId: Long) : ArtistListUiEvent()
    object NavigateUp : ArtistListUiEvent()
    data class OpenContextMenu(val artistId: Long) : ArtistListUiEvent()
    data class NavEvent(val navigationDestination: NavigationDestination) : ArtistListUiEvent()
}
