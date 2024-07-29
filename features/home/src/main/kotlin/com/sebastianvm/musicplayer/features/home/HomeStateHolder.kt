package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.core.ui.navigation.NavController
import com.sebastianvm.musicplayer.core.ui.navigation.UiComponent
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
    val trackListUiComponent: UiComponent<*>,
    val artistListUiComponent: UiComponent<*>,
    val albumListUiComponent: UiComponent<*>,
    val genreListUiComponent: UiComponent<*>,
    val playlistListUiComponent: UiComponent<*>,
    val searchUiComponent: UiComponent<*>,
) : State

sealed interface HomeUserAction : UserAction

class HomeStateHolder(
    features: FeatureRegistry,
    navController: NavController,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<HomeState, HomeUserAction> {

    private val _state =
        MutableStateFlow(
            HomeState(
                trackListUiComponent =
                    features.trackListFeature().trackListUiComponent(navController),
                artistListUiComponent = features.artistList().artistListUiComponent(navController),
                albumListUiComponent = features.albumList().albumListUiComponent(navController),
                genreListUiComponent = features.genreList().genreListUiComponent(navController),
                playlistListUiComponent =
                    features.playlistList().playlistListUiComponent(navController),
                searchUiComponent = features.searchFeature().searchUiComponent(navController),
            ))
    override val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    override fun handle(action: HomeUserAction) = Unit
}
