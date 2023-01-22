package com.sebastianvm.musicplayer.ui.library.artistlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.artist.ArtistRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.lists.ModelListItemState
import com.sebastianvm.musicplayer.ui.components.lists.toModelListItemState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArtistListViewModel @Inject constructor(
    initialState: ArtistListState,
    artistRepository: ArtistRepository,
    private val preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<ArtistListState, ArtistListUserAction>(initialState) {

    init {
        artistRepository.getArtists().onEach { artists ->
            setState {
                copy(
                    artistList = artists.map { artist ->
                        artist.toModelListItemState()
                    }
                )
            }
        }.launchIn(viewModelScope)
    }


    override fun handle(action: ArtistListUserAction) {
        when (action) {
            is ArtistListUserAction.SortByButtonClicked -> {
                viewModelScope.launch {
                    preferencesRepository.toggleArtistListSortOrder()
                }
            }
        }
    }
}

data class ArtistListState(val artistList: List<ModelListItemState>) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialArtistListStateModule {
    @Provides
    @ViewModelScoped
    fun initialArtistListStateProvider(): ArtistListState {
        return ArtistListState(artistList = listOf())
    }
}


sealed interface ArtistListUserAction : UserAction {
    object SortByButtonClicked : ArtistListUserAction
}