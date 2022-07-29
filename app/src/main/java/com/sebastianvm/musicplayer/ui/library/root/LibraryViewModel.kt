package com.sebastianvm.musicplayer.ui.library.root

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.music.MusicRepository
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListArguments
import com.sebastianvm.musicplayer.ui.library.tracks.TrackListViewModel
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.navigation.NavigationRoute
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    musicRepository: MusicRepository,
    initialState: LibraryState,
) : BaseViewModel<LibraryUiEvent, LibraryState>(initialState) {

    init {
        viewModelScope.launch {
            musicRepository.getCounts().collect { counts ->
                setState {
                    copy(libraryItems = libraryItems.map { item ->
                        when (item) {
                            is LibraryItem.Tracks -> item.copy(count = counts.tracks)
                            is LibraryItem.Artists -> item.copy(count = counts.artists)
                            is LibraryItem.Albums -> item.copy(count = counts.albums)
                            is LibraryItem.Genres -> item.copy(count = counts.genres)
                            is LibraryItem.Playlists -> item.copy(count = counts.playlists)
                        }
                    })
                }

            }
        }
    }

    fun onRowClicked(rowId: NavigationRoute) {
        when (rowId) {
            NavigationRoute.TrackList -> addNavEvent(
                NavEvent.NavigateToScreen(
                    NavigationDestination.TrackList(
                        TrackListArguments(
                            trackListId = TrackListViewModel.ALL_TRACKS,
                            trackListType = TrackListType.ALL_TRACKS
                        )
                    )
                )
            )
            NavigationRoute.ArtistsRoot -> addNavEvent(
                NavEvent.NavigateToScreen(
                    NavigationDestination.ArtistsRoot
                )
            )
            NavigationRoute.AlbumsRoot -> addNavEvent(
                NavEvent.NavigateToScreen(
                    NavigationDestination.AlbumsRoot
                )
            )
            NavigationRoute.GenresRoot -> addNavEvent(
                NavEvent.NavigateToScreen(
                    NavigationDestination.GenresRoot
                )
            )
            NavigationRoute.PlaylistsRoot -> addNavEvent(
                NavEvent.NavigateToScreen(
                    NavigationDestination.PlaylistsRoot
                )
            )
            else -> Unit
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
