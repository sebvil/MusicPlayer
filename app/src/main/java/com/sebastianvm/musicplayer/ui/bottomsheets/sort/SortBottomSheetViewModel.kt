package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackListType
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.navigation.NavArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.events.UiEvent
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.not
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(
    initialState: SortBottomSheetState,
    private val sortPreferencesRepository: SortPreferencesRepository
) :
    BaseViewModel<SortBottomSheetUiEvent, SortBottomSheetState>(
        initialState
    ) {

    init {
        viewModelScope.launch {
            val sortPreferences = when (state.value.selectedSort) {
                is SortOptions.TrackListSortOptions -> {
                    sortPreferencesRepository.getTrackListSortPreferences(
                        trackListType = TrackListType.ALL_TRACKS,
                        trackListId = state.value.mediaId
                    )
                }
                is SortOptions.AlbumListSortOptions -> {
                    sortPreferencesRepository.getAlbumListSortPreferences()
                }
                is SortOptions.PlaylistSortOptions -> {
                    sortPreferencesRepository.getPlaylistSortPreferences(playlistId = state.value.mediaId)
                }
            }.first()
            setState {
                copy(
                    selectedSort = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder
                )
            }
        }
    }

    fun onMediaSortOptionClicked(newSortOption: SortOptions) {
        val newSortOrder = if (newSortOption == state.value.selectedSort) {
            !state.value.sortOrder
        } else {
            state.value.sortOrder
        }
        viewModelScope.launch {
            when (newSortOption) {
                is SortOptions.TrackListSortOptions -> {
                    sortPreferencesRepository.modifyTrackListSortPreferences(
                        newPreferences = MediaSortPreferences(
                            sortOption = newSortOption,
                            sortOrder = newSortOrder
                        ),
                        trackListType = TrackListType.ALL_TRACKS,
                        trackListId = state.value.mediaId
                    )
                }
                is SortOptions.AlbumListSortOptions -> {
                    sortPreferencesRepository.modifyAlbumListSortPreferences(
                        newPreferences = MediaSortPreferences(
                            sortOption = newSortOption,
                            sortOrder = newSortOrder
                        )
                    )
                }
                is SortOptions.PlaylistSortOptions -> {
                    sortPreferencesRepository.modifyPlaylistsSortPreferences(
                        newPreferences = MediaSortPreferences(
                            sortOption = newSortOption,
                            sortOrder = newSortOrder
                        ),
                        playlistId = state.value.mediaId
                    )
                }
            }
            addUiEvent(SortBottomSheetUiEvent.CloseBottomSheet)
        }
    }
}

data class SortBottomSheetState(
    val mediaId: Long,
    val sortOptions: List<SortOptions>,
    val selectedSort: SortOptions,
    val sortOrder: MediaSortOrder,
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSortBottomSheetState {
    @Provides
    @ViewModelScoped
    fun initialSortBottomSheetStateProvider(savedStateHandle: SavedStateHandle): SortBottomSheetState {
        // We should always pass these
        val listType = SortableListType.valueOf(savedStateHandle[NavArgs.SORTABLE_LIST_TYPE]!!)
        val mediaId: Long = savedStateHandle[NavArgs.MEDIA_ID] ?: 0L
        val sortOptions = getSortOptionsForScreen(listType)
        return SortBottomSheetState(
            mediaId = mediaId,
            sortOptions = sortOptions,
            selectedSort = sortOptions[0],
            sortOrder = MediaSortOrder.ASCENDING,
        )
    }
}

fun getSortOptionsForScreen(listType: SortableListType): List<SortOptions> {
    return when (listType) {
        SortableListType.TRACKS -> {
            SortOptions.TrackListSortOptions.values().toList()
        }
        SortableListType.ALBUMS -> {
            SortOptions.AlbumListSortOptions.values().toList()
        }
        SortableListType.PLAYLIST -> {
            SortOptions.PlaylistSortOptions.values().toList()
        }
    }
}

enum class SortableListType {
    TRACKS,
    ALBUMS,
    PLAYLIST
}

sealed class SortBottomSheetUiEvent : UiEvent {
    object CloseBottomSheet : SortBottomSheetUiEvent()
}

