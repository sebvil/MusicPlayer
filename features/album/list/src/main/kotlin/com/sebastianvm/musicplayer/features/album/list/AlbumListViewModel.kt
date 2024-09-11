package com.sebastianvm.musicplayer.features.album.list

import androidx.lifecycle.viewModelScope
import com.sebastianvm.musicplayer.core.data.album.AlbumRepository
import com.sebastianvm.musicplayer.core.data.preferences.SortPreferencesRepository
import com.sebastianvm.musicplayer.core.designsystems.components.AlbumRow
import com.sebastianvm.musicplayer.core.designsystems.components.SortButton
import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.Data
import com.sebastianvm.musicplayer.core.ui.mvvm.Empty
import com.sebastianvm.musicplayer.core.ui.mvvm.Loading
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UiState
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.NavOptions
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsArguments
import com.sebastianvm.musicplayer.features.api.album.details.AlbumDetailsProps
import com.sebastianvm.musicplayer.features.api.album.details.albumDetails
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListProps
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuArguments
import com.sebastianvm.musicplayer.features.api.album.menu.AlbumContextMenuProps
import com.sebastianvm.musicplayer.features.api.album.menu.albumContextMenu
import com.sebastianvm.musicplayer.features.api.sort.SortMenuArguments
import com.sebastianvm.musicplayer.features.api.sort.SortableListType
import com.sebastianvm.musicplayer.features.api.sort.sortMenu
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
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

class AlbumListViewModel(
    vmScope: CoroutineScope = getViewModelScope(),
    albumRepository: AlbumRepository,
    sortPreferencesRepository: SortPreferencesRepository,
    private val props: StateFlow<AlbumListProps>,
    private val features: FeatureRegistry,
) : BaseViewModel<UiState<AlbumListState>, AlbumListUserAction>(viewModelScope = vmScope) {

    private val navController: NavController
        get() = props.value.navController

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
                        )
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, Loading)

    override fun handle(action: AlbumListUserAction) {
        when (action) {
            is AlbumListUserAction.AlbumMoreIconClicked -> {
                navController.push(
                    features
                        .albumContextMenu()
                        .create(
                            arguments = AlbumContextMenuArguments(action.albumId),
                            props =
                                MutableStateFlow(
                                    AlbumContextMenuProps(navController = navController)
                                ),
                        ),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.SortButtonClicked -> {
                navController.push(
                    features
                        .sortMenu()
                        .create(arguments = SortMenuArguments(listType = SortableListType.Albums)),
                    navOptions =
                        NavOptions(presentationMode = NavOptions.PresentationMode.BottomSheet),
                )
            }
            is AlbumListUserAction.AlbumClicked -> {
                navController.push(
                    features
                        .albumDetails()
                        .create(
                            arguments =
                                AlbumDetailsArguments(
                                    albumId = action.albumItem.id,
                                    albumName = action.albumItem.albumName,
                                    imageUri = action.albumItem.artworkUri,
                                    artists = action.albumItem.artists,
                                ),
                            props =
                                MutableStateFlow(AlbumDetailsProps(navController = navController)),
                        )
                )
            }
        }
    }
}
