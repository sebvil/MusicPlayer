package com.sebastianvm.musicplayer.features.home

import com.sebastianvm.musicplayer.core.ui.mvvm.State
import com.sebastianvm.musicplayer.core.ui.mvvm.StateHolder
import com.sebastianvm.musicplayer.core.ui.mvvm.UserAction
import com.sebastianvm.musicplayer.core.ui.mvvm.stateHolderScope
import com.sebastianvm.musicplayer.features.album.list.AlbumListUiComponent
import com.sebastianvm.musicplayer.features.artist.list.ArtistListUiComponent
import com.sebastianvm.musicplayer.features.genre.list.GenreListUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistListUiComponent
import com.sebastianvm.musicplayer.features.search.SearchUiComponent
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val trackListUiComponent: TrackListUiComponent,
    val artistListUiComponent: ArtistListUiComponent,
    val albumListUiComponent: AlbumListUiComponent,
    val genreListUiComponent: GenreListUiComponent,
    val playlistListUiComponent: PlaylistListUiComponent,
    val searchUiComponent: SearchUiComponent,
) : State

sealed interface HomeUserAction : UserAction

class HomeStateHolder(
    trackListUiComponent: TrackListUiComponent,
    artistListUiComponent: ArtistListUiComponent,
    albumListUiComponent: AlbumListUiComponent,
    genreListUiComponent: GenreListUiComponent,
    playlistListUiComponent: PlaylistListUiComponent,
    searchUiComponent: SearchUiComponent,
    override val stateHolderScope: CoroutineScope = stateHolderScope(),
) : StateHolder<HomeState, HomeUserAction> {

    private val _state =
        MutableStateFlow(
            HomeState(
                trackListUiComponent,
                artistListUiComponent,
                albumListUiComponent,
                genreListUiComponent,
                playlistListUiComponent,
                searchUiComponent,
            ))
    override val state: StateFlow<HomeState>
        get() = _state.asStateFlow()

    override fun handle(action: HomeUserAction) = Unit
}

fun getHomeStateHolder(navController: NavController): HomeStateHolder {
    val trackListUiComponent = TrackListUiComponent(navController)
    val artistListUiComponent = ArtistListUiComponent(navController)
    val albumListUiComponent = AlbumListUiComponent(navController)
    val genreListUiComponent = GenreListUiComponent(navController)
    val playlistListUiComponent = PlaylistListUiComponent(navController)
    val searchUiComponent = SearchUiComponent(navController)
    return HomeStateHolder(
        trackListUiComponent = trackListUiComponent,
        artistListUiComponent = artistListUiComponent,
        albumListUiComponent = albumListUiComponent,
        genreListUiComponent = genreListUiComponent,
        playlistListUiComponent = playlistListUiComponent,
        searchUiComponent = searchUiComponent,
    )
}
