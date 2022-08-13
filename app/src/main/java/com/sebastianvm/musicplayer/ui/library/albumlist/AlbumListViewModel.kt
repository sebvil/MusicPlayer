package com.sebastianvm.musicplayer.ui.library.albumlist

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.album.AlbumArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.context.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortMenuArguments
import com.sebastianvm.musicplayer.ui.bottomsheets.sort.SortableListType
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.navigation.NavigationDestination
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.ViewModelInterface
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
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
) : BaseViewModel<AlbumListUiEvent, AlbumListState>(initialState),
    ViewModelInterface<AlbumListState, AlbumListUserAction> {

    init {
        viewModelScope.launch {
            preferencesRepository.getAlbumListSortPreferences().flatMapLatest {
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

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.Album(
                            AlbumArguments(action.albumId)
                        )
                    )
                )
            }
            is AlbumListUserAction.AlbumOverflowIconClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.AlbumContextMenu(
                            AlbumContextMenuArguments(action.albumId)
                        )
                    )
                )
            }
            AlbumListUserAction.SortByClicked -> {
                addNavEvent(
                    NavEvent.NavigateToScreen(
                        NavigationDestination.SortMenu(
                            SortMenuArguments(
                                listType = SortableListType.Albums
                            )
                        )
                    )
                )
            }
            AlbumListUserAction.UpButtonClicked -> addNavEvent(NavEvent.NavigateUp)
        }
    }
}

data class AlbumListState(val albumList: List<AlbumRowState>) : State


@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumListState {
        return AlbumListState(albumList = listOf())
    }
}

sealed class AlbumListUiEvent : UiEvent {
    object ScrollToTop : AlbumListUiEvent()
}

sealed interface AlbumListUserAction : UserAction {
    data class AlbumClicked(val albumId: Long) : AlbumListUserAction
    object UpButtonClicked : AlbumListUserAction
    object SortByClicked : AlbumListUserAction
    data class AlbumOverflowIconClicked(val albumId: Long) : AlbumListUserAction
}