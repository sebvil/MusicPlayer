package com.sebastianvm.musicplayer.features.sort

import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.datastore.sort.MediaSortPreferences
import com.sebastianvm.musicplayer.core.model.MediaGroup
import com.sebastianvm.musicplayer.core.model.MediaSortOrder
import com.sebastianvm.musicplayer.core.model.SortOptions
import com.sebastianvm.musicplayer.core.model.not
import com.sebastianvm.musicplayer.services.Services
import com.sebastianvm.musicplayer.services.features.mvvm.State
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.mvvm.UserAction
import com.sebastianvm.musicplayer.services.features.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.services.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.services.features.sort.SortableListType
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

    private val sortPreferences =
        when (val listType = arguments.listType) {
            is SortableListType.AllTracks -> {
                sortPreferencesRepository.getTrackListSortPreferences(
                    trackList = MediaGroup.AllTracks)
            }
            is SortableListType.Genre -> {
                sortPreferencesRepository.getTrackListSortPreferences(
                    trackList = MediaGroup.Genre(listType.genreId))
            }
            is SortableListType.Albums -> {
                sortPreferencesRepository.getAlbumListSortPreferences()
            }
            is SortableListType.Playlist -> {
                sortPreferencesRepository.getPlaylistSortPreferences(
                    playlistId = listType.playlistId)
            }
        }
    private val sortOptions = getSortOptionsForScreen(arguments.listType)

    override val state: StateFlow<SortMenuState> =
        sortPreferences
            .map { sortPreferences ->
                SortMenuState(
                    sortOptions = sortOptions,
                    selectedSort = sortPreferences.sortOption,
                    sortOrder = sortPreferences.sortOrder,
                )
            }
            .stateIn(
                stateHolderScope,
                SharingStarted.Lazily,
                SortMenuState(
                    sortOptions = sortOptions,
                    selectedSort = null,
                    sortOrder = MediaSortOrder.ASCENDING,
                ),
            )

    override fun handle(action: SortMenuUserAction) {
        when (action) {
            is SortMenuUserAction.MediaSortOptionClicked -> {
                val newSortOption = action.newSortOption
                val selectedSort = action.selectedSort
                val sortOrder = action.currentSortOrder
                val newSortOrder =
                    if (newSortOption == selectedSort) {
                        !sortOrder
                    } else {
                        sortOrder
                    }
                stateHolderScope.launch {
                    when (val listType = arguments.listType) {
                        is SortableListType.AllTracks -> {
                            require(newSortOption is SortOptions.TrackListSortOption) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyTrackListSortPreferences(
                                newPreferences =
                                    MediaSortPreferences(
                                        sortOption = newSortOption,
                                        sortOrder = newSortOrder,
                                    ),
                                trackList = MediaGroup.AllTracks,
                            )
                        }
                        is SortableListType.Genre -> {
                            require(newSortOption is SortOptions.TrackListSortOption) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyTrackListSortPreferences(
                                newPreferences =
                                    MediaSortPreferences(
                                        sortOption = newSortOption,
                                        sortOrder = newSortOrder,
                                    ),
                                trackList = MediaGroup.Genre(listType.genreId),
                            )
                        }
                        is SortableListType.Albums -> {
                            require(newSortOption is SortOptions.AlbumListSortOption) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyAlbumListSortPreferences(
                                newPreferences =
                                    MediaSortPreferences(
                                        sortOption = newSortOption,
                                        sortOrder = newSortOrder,
                                    ))
                        }
                        is SortableListType.Playlist -> {
                            require(newSortOption is SortOptions.PlaylistSortOption) {
                                "Invalid SortOptions type ${newSortOption.javaClass} for list type $listType"
                            }
                            sortPreferencesRepository.modifyPlaylistsSortPreferences(
                                newPreferences =
                                    MediaSortPreferences(
                                        sortOption = newSortOption,
                                        sortOrder = newSortOrder,
                                    ),
                                playlistId = listType.playlistId,
                            )
                        }
                    }
                }
            }
        }
    }
}

data class SortMenuState(
    val sortOptions: List<SortOptions>,
    val selectedSort: SortOptions?,
    val sortOrder: MediaSortOrder,
) : State

private fun getSortOptionsForScreen(listType: SortableListType): List<SortOptions> {
    return when (listType) {
        is SortableListType.AllTracks,
        is SortableListType.Genre -> {
            SortOptions.forTracks
        }
        is SortableListType.Albums -> {
            SortOptions.forAlbums
        }
        is SortableListType.Playlist -> {
            SortOptions.forPlaylist
        }
    }
}

sealed interface SortMenuUserAction : UserAction {
    data class MediaSortOptionClicked(
        val newSortOption: SortOptions,
        val selectedSort: SortOptions,
        val currentSortOrder: MediaSortOrder,
    ) : SortMenuUserAction
}

fun getSortMenuStateHolder(services: Services, arguments: SortMenuArguments): SortMenuStateHolder {
    return SortMenuStateHolder(
        arguments = arguments,
        sortPreferencesRepository = services.repositoryProvider.sortPreferencesRepository,
    )
}
