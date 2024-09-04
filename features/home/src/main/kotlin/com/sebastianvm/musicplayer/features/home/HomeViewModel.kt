package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.mvvm.BaseViewModel
import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.UiComponent
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.getViewModelScope
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListArguments
import com.sebastianvm.musicplayer.features.api.album.list.AlbumListProps
import com.sebastianvm.musicplayer.features.api.album.list.albumList
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListArguments
import com.sebastianvm.musicplayer.features.api.artist.list.ArtistListProps
import com.sebastianvm.musicplayer.features.api.artist.list.artistList
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListArguments
import com.sebastianvm.musicplayer.features.api.genre.list.GenreListProps
import com.sebastianvm.musicplayer.features.api.genre.list.genreList
import com.sebastianvm.musicplayer.features.api.home.HomeProps
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListArguments
import com.sebastianvm.musicplayer.features.api.playlist.list.PlaylistListProps
import com.sebastianvm.musicplayer.features.api.playlist.list.playlistList
import com.sebastianvm.musicplayer.features.api.search.SearchArguments
import com.sebastianvm.musicplayer.features.api.search.SearchProps
import com.sebastianvm.musicplayer.features.api.search.searchFeature
import com.sebastianvm.musicplayer.features.api.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.api.track.list.TrackListProps
import com.sebastianvm.musicplayer.features.api.track.list.trackListFeature
import com.sebastianvm.musicplayer.features.registry.FeatureRegistry
import com.sebastianvm.musicplayer.features.registry.create
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val trackListMvvmComponent: UiComponent,
    val artistListMvvmComponent: UiComponent,
    val albumListMvvmComponent: UiComponent,
    val genreListMvvmComponent: UiComponent,
    val playlistListMvvmComponent: UiComponent,
    val searchMvvmComponent: UiComponent,
) : State

sealed interface HomeUserAction : UserAction

class HomeViewModel(
    props: StateFlow<HomeProps>,
    features: FeatureRegistry,
    vmScope: CoroutineScope = getViewModelScope(),
) : BaseViewModel<HomeState, HomeUserAction>(viewModelScope = vmScope) {

    private val navController = props.value.navController
    private val _state =
        MutableStateFlow(
            HomeState(
                trackListMvvmComponent =
                    features
                        .trackListFeature()
                        .create(
                            arguments = TrackListArguments,
                            props = MutableStateFlow(TrackListProps(navController)),
                        ),
                artistListMvvmComponent =
                    features
                        .artistList()
                        .create(
                            arguments = ArtistListArguments,
                            props = MutableStateFlow(ArtistListProps(navController = navController)),
                        ),
                albumListMvvmComponent =
                    features
                        .albumList()
                        .create(
                            arguments = AlbumListArguments,
                            props = MutableStateFlow(AlbumListProps(navController = navController)),
                        ),
                genreListMvvmComponent =
                    features
                        .genreList()
                        .create(
                            arguments = GenreListArguments,
                            props = MutableStateFlow(GenreListProps(navController = navController)),
                        ),
                playlistListMvvmComponent =
                    features
                        .playlistList()
                        .create(
                            arguments = PlaylistListArguments,
                            props = MutableStateFlow(PlaylistListProps(navController)),
                        ),
                searchMvvmComponent =
                    features
                        .searchFeature()
                        .create(
                            arguments = SearchArguments,
                            props = MutableStateFlow(SearchProps(navController)),
                        ),
            )
        )
    override val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    override fun handle(action: HomeUserAction) = Unit
}
