package com.sebastianvm.musicplayer.ui.bottomsheets.sort

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.navArgs
import com.sebastianvm.musicplayer.ui.util.mvvm.DeprecatedBaseViewModel
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.mvvm.events.NavEvent
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
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class SortBottomSheetViewModel @Inject constructor(
    initialState: SortBottomSheetState,
    private val sortPreferencesRepository: SortPreferencesRepository
) : DeprecatedBaseViewModel<SortBottomSheetState, SortBottomSheetUserAction>(
    initialState
) {

    init {
        viewModelScope.launch {
            val sortPreferences = when (val listType = state.listType) {
                is SortableListType.Tracks -> {
                    sortPreferencesRepository.getTrackListSortPreferences(
                        trackList = listType.trackList,
                    )
                }

                is SortableListType.Albums -> {
                    sortPreferencesRepository.getAlbumListSortPreferences()
                }

                is SortableListType.Playlist -> {
                    sortPreferencesRepository.getPlaylistSortPreferences(playlistId = listType.playlistId)
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

    override fun handle(action: SortBottomSheetUserAction) {
        when (action) {
            is SortBottomSheetUserAction.MediaSortOptionClicked -> {
                val newSortOption = action.newSortOption
                val newSortOrder = if (newSortOption == state.selectedSort) {
                    !state.sortOrder
                } else {
                    state.sortOrder
                }
                viewModelScope.launch {
                    when (val listType = state.listType) {
                        is SortableListType.Tracks -> {
                            require(newSortOption is SortOptions.TrackListSortOptions) { "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType" }
                            sortPreferencesRepository.modifyTrackListSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                ),
                                trackList = listType.trackList,
                            )
                        }

                        is SortableListType.Albums -> {
                            require(newSortOption is SortOptions.AlbumListSortOptions) { "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType" }
                            sortPreferencesRepository.modifyAlbumListSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                )
                            )
                        }

                        is SortableListType.Playlist -> {
                            require(newSortOption is SortOptions.PlaylistSortOptions) { "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType" }
                            sortPreferencesRepository.modifyPlaylistsSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                ),
                                playlistId = listType.playlistId
                            )
                        }
                    }
                    addNavEvent(NavEvent.NavigateUp)
                }
            }
        }
    }
}

data class SortMenuArguments(val listType: SortableListType)

data class SortBottomSheetState(
    val sortOptions: List<SortOptions>,
    val selectedSort: SortOptions,
    val sortOrder: MediaSortOrder,
    val listType: SortableListType
) : State

@InstallIn(ViewModelComponent::class)
@Module
object InitialSortBottomSheetState {
    @Provides
    @ViewModelScoped
    fun initialSortBottomSheetStateProvider(savedStateHandle: SavedStateHandle): SortBottomSheetState {
        val args = savedStateHandle.navArgs<SortMenuArguments>()
        val sortOptions = getSortOptionsForScreen(args.listType)
        return SortBottomSheetState(
            sortOptions = sortOptions,
            selectedSort = sortOptions[0],
            sortOrder = MediaSortOrder.ASCENDING,
            listType = args.listType
        )
    }
}

private fun getSortOptionsForScreen(listType: SortableListType): List<SortOptions> {
    return when (listType) {
        is SortableListType.Tracks -> {
            SortOptions.TrackListSortOptions.values().toList()
        }

        is SortableListType.Albums -> {
            SortOptions.AlbumListSortOptions.values().toList()
        }

        is SortableListType.Playlist -> {
            SortOptions.PlaylistSortOptions.values().toList()
        }
    }
}

@Serializable
@Parcelize
sealed class SortableListType : Parcelable {
    @Serializable
    data class Tracks(val trackList: TrackList) : SortableListType()

    @Serializable
    data object Albums : SortableListType()

    @Serializable
    data class Playlist(val playlistId: Long) : SortableListType()

}

sealed interface SortBottomSheetUserAction : UserAction {
    data class MediaSortOptionClicked(val newSortOption: SortOptions) : SortBottomSheetUserAction
}
