package com.sebastianvm.musicplayer.ui.library.root

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicRepository: MusicRepository,
    initialState: LibraryState,
) : BaseViewModel<LibraryState, LibraryUserAction, LibraryUiEvent>(initialState) {

    init {
        musicRepository.getCounts().onEach { counts ->
            setState {
                copy(
                    libraryItems = listOf(
                        LibraryItem.Tracks(count = counts.tracks),
                        LibraryItem.Artists(count = counts.artists),
                        LibraryItem.Albums(count = counts.albums),
                        LibraryItem.Genres(count = counts.genres),
                        LibraryItem.Playlists(count = counts.playlists),
                    )
                )
            }
        }.launchIn(viewModelScope)
    }

    override fun handle(action: LibraryUserAction) {
        when (action) {
            is LibraryUserAction.RowClicked -> {
                addNavEvent(NavEvent.NavigateToScreen(action.destination))
            }

            is LibraryUserAction.SearchBoxClicked -> {
                addNavEvent(NavEvent.NavigateToScreen(NavigationDestination.Search))
            }
        }
    }
}


data class LibraryState(val libraryItems: List<LibraryItem>) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialLibraryStateModule {
    @Provides
    @ViewModelScoped
    fun initialLibraryStateProvider() = LibraryState(
        libraryItems = listOf(
            LibraryItem.Tracks(count = 0),
            LibraryItem.Artists(count = 0),
            LibraryItem.Albums(count = 0),
            LibraryItem.Genres(count = 0),
            LibraryItem.Playlists(count = 0)
        )
    )
}

sealed class LibraryUiEvent : UiEvent {
    object RequestPermission : LibraryUiEvent()
}

sealed class LibraryUserAction : UserAction {
    data class RowClicked(val destination: NavigationDestination) : LibraryUserAction()
    object SearchBoxClicked : LibraryUserAction()
}
