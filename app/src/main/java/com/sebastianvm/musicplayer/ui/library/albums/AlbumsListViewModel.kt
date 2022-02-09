package com.sebastianvm.musicplayer.ui.library.albums

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.repository.album.AlbumRepository
import com.sebastianvm.musicplayer.repository.preferences.PreferencesRepository
import com.sebastianvm.musicplayer.ui.components.AlbumRowState
import com.sebastianvm.musicplayer.ui.components.toAlbumRowState
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.util.sort.getStringComparator
import com.sebastianvm.musicplayer.util.sort.id
import com.sebastianvm.musicplayer.util.sort.not
import com.sebastianvm.musicplayer.util.sort.MediaSortOption
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.mediaSortSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlbumsListViewModel @Inject constructor(
    initialState: AlbumsListState,
    albumRepository: AlbumRepository,
    private val preferencesRepository: PreferencesRepository,
) : BaseViewModel<AlbumsListUserAction, AlbumsListUiEvent, AlbumsListState>(initialState) {

    init {
        collect(preferencesRepository.getAlbumsListSortOptions()) { settings ->
            setState {
                copy(
                    currentSort = settings.sortOption,
                    albumsList = albumsList.sortedWith(
                        getComparator(
                            settings.sortOrder,
                            settings.sortOption
                        )
                    ),
                    sortOrder = settings.sortOrder
                )
            }

        }
        collect(albumRepository.getAlbums()) { albums ->
            setState {
                copy(
                    albumsList = albums.map { album ->
                        album.toAlbumRowState()
                    }.sortedWith(getComparator(sortOrder, currentSort)),
                )
            }
        }
    }


    override fun handle(action: AlbumsListUserAction) {
        when (action) {
            is AlbumsListUserAction.AlbumClicked -> {
                addUiEvent(
                    AlbumsListUiEvent.NavigateToAlbum(action.albumId)
                )
            }
            is AlbumsListUserAction.UpButtonClicked -> addUiEvent(AlbumsListUiEvent.NavigateUp)
            is AlbumsListUserAction.SortByClicked -> {
                addUiEvent(
                    AlbumsListUiEvent.ShowSortBottomSheet(
                        state.value.currentSort.id,
                        state.value.sortOrder
                    )
                )
            }
            is AlbumsListUserAction.MediaSortOptionClicked -> {
                val sortOrder = if (action.newSortOption == state.value.currentSort) {
                    !state.value.sortOrder
                } else {
                    state.value.sortOrder
                }

                viewModelScope.launch {
                    preferencesRepository.modifyAlbumsListSortOptions(
                        mediaSortSettings = mediaSortSettings {
                            sortOption = action.newSortOption
                            this.sortOrder = sortOrder
                        },
                    )
                    addUiEvent(AlbumsListUiEvent.ScrollToTop)
                }
            }
            is AlbumsListUserAction.AlbumContextButtonClicked -> {
                addUiEvent(AlbumsListUiEvent.OpenContextMenu(action.albumId))
            }
        }
    }

    private fun getComparator(
        sortOrder: MediaSortOrder,
        sortOption: MediaSortOption
    ): Comparator<AlbumRowState> {
        return getStringComparator(sortOrder) { albumRowState ->
            when (sortOption) {
                MediaSortOption.ARTIST -> albumRowState.artists
                MediaSortOption.ALBUM -> albumRowState.albumName
                MediaSortOption.YEAR -> albumRowState.year.toString()
                else -> throw IllegalStateException("Unknown sort option for Albums list: $sortOption")
            }
        }
    }
}

data class AlbumsListState(
    val albumsList: List<AlbumRowState>,
    val currentSort: MediaSortOption,
    val sortOrder: MediaSortOrder,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialAlbumsListStateModule {
    @Provides
    @ViewModelScoped
    fun initialAlbumsStateProvider(): AlbumsListState {
        return AlbumsListState(
            albumsList = listOf(),
            currentSort = MediaSortOption.ALBUM,
            sortOrder = MediaSortOrder.ASCENDING
        )
    }
}

sealed class AlbumsListUserAction : UserAction {
    data class AlbumClicked(val albumId: String) : AlbumsListUserAction()
    object UpButtonClicked : AlbumsListUserAction()
    object SortByClicked : AlbumsListUserAction()
    data class MediaSortOptionClicked(val newSortOption: MediaSortOption) : AlbumsListUserAction()
    data class AlbumContextButtonClicked(val albumId: String) : AlbumsListUserAction()
}

sealed class AlbumsListUiEvent : UiEvent {
    data class NavigateToAlbum(val albumId: String) : AlbumsListUiEvent()
    object NavigateUp : AlbumsListUiEvent()
    data class ShowSortBottomSheet(@StringRes val sortOption: Int, val sortOrder: MediaSortOrder) :
        AlbumsListUiEvent()

    object ScrollToTop : AlbumsListUiEvent()

    data class OpenContextMenu(val albumId: String) : AlbumsListUiEvent()
}
