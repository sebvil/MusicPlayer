package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.MvvmComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.features.api.album.list.albumList
import com.sebastianvm.musicplayer.features.api.artist.list.artistList
import com.sebastianvm.musicplayer.features.api.genre.list.genreList
import com.sebastianvm.musicplayer.features.api.playlist.list.playlistList
import com.sebastianvm.musicplayer.features.api.search.searchFeature
import com.sebastianvm.musicplayer.features.api.track.list.trackListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val trackListMvvmComponent: MvvmComponent,
    val artistListMvvmComponent: MvvmComponent,
    val albumListMvvmComponent: MvvmComponent,
    val genreListMvvmComponent: MvvmComponent,
    val playlistListMvvmComponent: MvvmComponent,
    val searchMvvmComponent: MvvmComponent,
) : State

sealed interface HomeUserAction : UserAction

class HomeViewModel(
    features: FeatureRegistry,
    navController: NavController,
    vmScope: CoroutineScope = getViewModelScope(),
) : BaseViewModel<HomeState, HomeUserAction>(viewModelScope = vmScope) {

    private val _state =
        MutableStateFlow(
            HomeState(
                trackListMvvmComponent =
                features.trackListFeature().trackListUiComponent(navController),
                artistListMvvmComponent = features.artistList()
                    .artistListUiComponent(navController),
                albumListMvvmComponent = features.albumList().albumListUiComponent(navController),
                genreListMvvmComponent = features.genreList().genreListUiComponent(navController),
                playlistListMvvmComponent =
                features.playlistList().playlistListUiComponent(navController),
                searchMvvmComponent = features.searchFeature().searchUiComponent(navController),
            )
        )
    override val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    override fun handle(action: HomeUserAction) = Unit
}
