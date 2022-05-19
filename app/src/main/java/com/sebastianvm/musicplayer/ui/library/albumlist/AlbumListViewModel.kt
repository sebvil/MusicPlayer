package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
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
class AlbumListViewModel @Inject constructor(
    initialState: AlbumListState,
    albumRepository: AlbumRepository,
    preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<AlbumListUiEvent, AlbumListState>(initialState) {

    init {
        viewModelScope.launch {
            preferencesRepository.getAlbumListSortPreferences().flatMapLatest {
                setState {
                    copy(
                        sortPreferences = it
                    )
                }
                albumRepository.getAlbums(sortPreferences = it)
            }.collect { albums ->
                setState {
                    copy(
                        albumList = albums.map { album ->
                            album.toAlbumRowState()
                        }
                    )
                }
            }
        }
    }

    fun onAlbumClicked(albumId: Long) {
        addUiEvent(
            AlbumListUiEvent.NavEvent(
                NavigationDestination.AlbumDestination(
                    AlbumArguments(albumId)
                )
            )
        )
    }

    fun onUpButtonClicked() {
        addUiEvent(AlbumListUiEvent.NavigateUp)
    }

    fun onSortByClicked() {
        addUiEvent(AlbumListUiEvent.ShowSortBottomSheet)
    }

    fun onAlbumOverflowMenuIconClicked(albumId: Long) {
        addUiEvent(AlbumListUiEvent.OpenContextMenu(albumId))
    }
}

data class AlbumListState(
    val albumList: List<AlbumRowState>,
    val sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumListState {
        return AlbumListState(
            albumList = listOf(),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.AlbumListSortOptions.ALBUM)
        )
    }
}

sealed class AlbumListUiEvent : UiEvent {
    data class NavEvent(val navigationDestination: NavigationDestination) : AlbumListUiEvent()
    object NavigateUp : AlbumListUiEvent()
    object ShowSortBottomSheet : AlbumListUiEvent()
    object ScrollToTop : AlbumListUiEvent()
    data class OpenContextMenu(val albumId: Long) : AlbumListUiEvent()
}
