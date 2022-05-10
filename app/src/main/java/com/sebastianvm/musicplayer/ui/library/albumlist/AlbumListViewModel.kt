package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
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
    initialState: AlbumsListState,
    albumRepository: AlbumRepository,
    preferencesRepository: SortPreferencesRepository,
) : BaseViewModel<AlbumsListUiEvent, AlbumsListState>(initialState) {

    init {
        viewModelScope.launch {
            preferencesRepository.getAlbumsListSortPreferences().flatMapLatest {
                setState {
                    copy(
                        sortPreferences = it
                    )
                }
                albumRepository.getAlbums(sortPreferences = it)
            }.collect { albums ->
                setState {
                    copy(
                        albumsList = albums.map { album ->
                            album.toAlbumRowState()
                        }
                    )
                }
            }
        }
    }

    fun onAlbumClicked(albumId: Long) {
        addUiEvent(AlbumsListUiEvent.NavigateToAlbum(albumId))
    }

    fun onUpButtonClicked() {
        addUiEvent(AlbumsListUiEvent.NavigateUp)
    }

    fun onSortByClicked() {
        addUiEvent(AlbumsListUiEvent.ShowSortBottomSheet)
    }

    fun onAlbumOverflowMenuIconClicked(albumId: Long) {
        addUiEvent(AlbumsListUiEvent.OpenContextMenu(albumId))
    }
}

data class AlbumsListState(
    val albumsList: List<AlbumRowState>,
    val sortPreferences: MediaSortPreferences<SortOptions.AlbumListSortOptions>
) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumsListState {
        return AlbumsListState(
            albumsList = listOf(),
            sortPreferences = MediaSortPreferences(sortOption = SortOptions.AlbumListSortOptions.ALBUM)
        )
    }
}

sealed class AlbumsListUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: Long) : AlbumsListUiEvent()
    object NavigateUp : AlbumsListUiEvent()
    object ShowSortBottomSheet : AlbumsListUiEvent()
    object ScrollToTop : AlbumsListUiEvent()
    data class OpenContextMenu(val albumId: Long) : AlbumsListUiEvent()
}
