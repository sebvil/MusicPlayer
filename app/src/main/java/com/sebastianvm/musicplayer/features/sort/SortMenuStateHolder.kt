package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.player.TrackList
import com.sebastianvm.musicplayer.repository.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.ui.util.mvvm.Arguments
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import com.sebastianvm.musicplayer.util.sort.MediaSortOrder
import com.sebastianvm.musicplayer.util.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.util.sort.SortOptions
import com.sebastianvm.musicplayer.util.sort.not
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SortMenuStateHolder(
    private val arguments: SortMenuArguments,
    private val sortPreferencesRepository: SortPreferencesRepository,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<SortMenuState, SortMenuUserAction> {

    private val sortPreferences = when (val listType = arguments.listType) {
        is SortableListType.Tracks -> {
            sortPreferencesRepository.getTrackListSortPreferences(
                trackList = listType.trackList
            )
        }

        is SortableListType.Albums -> {
            sortPreferencesRepository.getAlbumListSortPreferences()
        }

        is SortableListType.Playlist -> {
            sortPreferencesRepository.getPlaylistSortPreferences(playlistId = listType.playlistId)
        }
    }
    private val sortOptions = getSortOptionsForScreen(arguments.listType)

    override val state: StateFlow<SortMenuState> =
        sortPreferences.map { sortPreferences ->
            SortMenuState(
                sortOptions = sortOptions,
                selectedSort = sortPreferences.sortOption,
                sortOrder = sortPreferences.sortOrder,
            )
        }.stateIn(
            stateHolderScope,
            SharingStarted.Lazily,
            SortMenuState(
                sortOptions = sortOptions,
                selectedSort = null,
                sortOrder = MediaSortOrder.ASCENDING,
            )
        )

    override fun handle(action: SortMenuUserAction) {
        when (action) {
            is SortMenuUserAction.MediaSortOptionClicked -> {
                val newSortOption = action.newSortOption
                val selectedSort = action.selectedSort
                val sortOrder = action.currentSortOrder
                val newSortOrder = if (newSortOption == selectedSort) {
                    !sortOrder
                } else {
                    sortOrder
                }
                stateHolderScope.launch {
                    when (val listType = arguments.listType) {
                        is SortableListType.Tracks -> {
                            require(newSortOption is SortOptions.TrackListSortOptions) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyTrackListSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                ),
                                trackList = listType.trackList
                            )
                        }

                        is SortableListType.Albums -> {
                            require(newSortOption is SortOptions.AlbumListSortOptions) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyAlbumListSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                )
                            )
                        }

                        is SortableListType.Playlist -> {
                            require(newSortOption is SortOptions.PlaylistSortOptions) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyPlaylistsSortPreferences(
                                newPreferences = MediaSortPreferences(
                                    sortOption = newSortOption,
                                    sortOrder = newSortOrder
                                ),
                                playlistId = listType.playlistId
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SortMenuArguments(val listType: SortableListType) : Arguments

data class SortMenuState(
    val sortOptions: List<SortOptions>,
    val selectedSort: SortOptions?,
    val sortOrder: MediaSortOrder,
) : State

private fun getSortOptionsForScreen(listType: SortableListType): List<SortOptions> {
    return when (listType) {
        is SortableListType.Tracks -> {
            SortOptions.TrackListSortOptions.entries
        }

        is SortableListType.Albums -> {
            SortOptions.AlbumListSortOptions.entries
        }

        is SortableListType.Playlist -> {
            SortOptions.PlaylistSortOptions.entries
        }
    }
}

sealed interface SortableListType {
    data class Tracks(val trackList: TrackList) : SortableListType
    data object Albums : SortableListType
    data class Playlist(val playlistId: Long) : SortableListType
}

sealed interface SortMenuUserAction : UserAction {
    data class MediaSortOptionClicked(
        val newSortOption: SortOptions,
        val selectedSort: SortOptions,
        val currentSortOrder: MediaSortOrder,
    ) : SortMenuUserAction
}

fun getSortMenuStateHolder(
    dependencies: DependencyContainer,
    arguments: SortMenuArguments
): SortMenuStateHolder {
    return SortMenuStateHolder(
        arguments = arguments,
        sortPreferencesRepository = dependencies.repositoryProvider.sortPreferencesRepository,
    )
}
