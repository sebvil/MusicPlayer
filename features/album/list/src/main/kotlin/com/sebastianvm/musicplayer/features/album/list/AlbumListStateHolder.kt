package com.sebastianvm.musicplayer.features.album.list

import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.services.features.Features
import com.sebastianvm.musicplayer.services.features.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.services.features.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.services.features.mvvm.Data
import com.sebastianvm.musicplayer.services.features.mvvm.Empty
import com.sebastianvm.musicplayer.services.features.mvvm.Loading
import com.sebastianvm.musicplayer.services.features.mvvm.State
import com.sebastianvm.musicplayer.services.features.mvvm.StateHolder
import com.sebastianvm.musicplayer.services.features.mvvm.UiState
import com.sebastianvm.musicplayer.services.features.mvvm.UserAction
import com.sebastianvm.musicplayer.services.features.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.services.features.navigation.NavController
import com.sebastianvm.musicplayer.services.features.navigation.NavOptions
import com.sebastianvm.musicplayer.services.features.sort.SortMenuArguments
import com.sebastianvm.musicplayer.services.features.sort.SortableListType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class AlbumListState(
    val albums: List<AlbumRow.State> = listOf(),
    val sortButtonState: SortButton.State,
) : State

sealed interface AlbumListUserAction : UserAction {
    data class AlbumMoreIconClicked(val albumId: Long) : AlbumListUserAction

    data object SortButtonClicked : AlbumListUserAction

    data class AlbumClicked(val albumItem: AlbumRow.State) : AlbumListUserAction
}

class AlbumListStateHolder(
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val navController: NavController,
    private val features: Features,
) : StateHolder<UiState<AlbumListState>, AlbumListUserAction> {

    override val state: StateFlow<UiState<AlbumListState>> =
        combine(
                albumRepository.getAlbums(),
                sortPreferencesRepository.getAlbumListSortPreferences(),
            ) { albums, sortPrefs ->
                if (albums.isEmpty()) {
                    Empty
                } else {
                    Data(
                        AlbumListState(
                            albums = albums.map { album -> AlbumRow.State.fromAlbum(album) },
                            sortButtonState =
                                SortButton.State(
                                    option = sortPrefs.sortOption,
                                    sortOrder = sortPrefs.sortOrder,
                                ),
                        ))
                }
            }
            .stateIn(stateHolderScope, SharingStarted.Lazily, Loading)

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    features.albumContextMenuFeature.albumContextMenuUiComponent(
                        arguments = AlbumContextMenuArguments(action.albumId),
                        navController = navController,
                    ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.SortButtonClicked -> {
                navController.push(
                    features.sortMenuFeature.sortMenuUiComponent(
                        arguments = SortMenuArguments(listType = SortableListType.Albums)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.AlbumClicked -> {
                navController.push(
                    features.albumDetailsFeature.albumDetailsUiComponent(
                        arguments =
                            AlbumDetailsArguments(
                                albumId = action.albumItem.id,
                                albumName = action.albumItem.albumName,
                                imageUri = action.albumItem.artworkUri,
                                artists = action.albumItem.artists,
                            ),
                        navController = navController,
                    ))
            }
        }
    }
}
