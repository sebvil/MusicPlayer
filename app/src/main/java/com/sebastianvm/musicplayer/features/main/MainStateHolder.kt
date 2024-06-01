package com.sebastianvm.musicplayer.features.main

import com.sebastianvm.musicplayer.features.album.list.AlbumListUiComponent
import com.sebastianvm.musicplayer.features.artist.list.ArtistListUiComponent
import com.sebastianvm.musicplayer.features.genre.list.GenreListUiComponent
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistListUiComponent
import com.sebastianvm.musicplayer.features.search.SearchUiComponent
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListUiComponent
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
import com.sebastianvm.musicplayer.ui.util.stateHolderScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainState(
    val trackListUiComponent: TrackListUiComponent,
    val artistListUiComponent: ArtistListUiComponent,
    val albumListUiComponent: AlbumListUiComponent,
    val genreListUiComponent: GenreListUiComponent,
    val playlistListUiComponent: PlaylistListUiComponent,
    val searchUiComponent: SearchUiComponent
) : State

sealed interface MainUserAction : UserAction

class MainStateHolder(
    trackListUiComponent: TrackListUiComponent,
    artistListUiComponent: ArtistListUiComponent,
    albumListUiComponent: AlbumListUiComponent,
    genreListUiComponent: GenreListUiComponent,
    playlistListUiComponent: PlaylistListUiComponent,
    searchUiComponent: SearchUiComponent,
    override val stateHolderScope: CoroutineScope = stateHolderScope()
) : StateHolder<MainState, MainUserAction> {

    private val _state = MutableStateFlow(
        MainState(
            trackListUiComponent,
            artistListUiComponent,
            albumListUiComponent,
            genreListUiComponent,
            playlistListUiComponent,
            searchUiComponent
        )
    )
    override val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    override fun handle(action: MainUserAction) = Unit
}

fun getMainStateHolder(
    navController: NavController
): MainStateHolder {
    val trackListUiComponent = TrackListUiComponent(
        arguments = TrackListArguments(trackListType = MediaGroup.AllTracks),
        navController = navController
    )
    val artistListUiComponent = ArtistListUiComponent(navController)
    val albumListUiComponent = AlbumListUiComponent(navController)
    val genreListUiComponent = GenreListUiComponent(navController)
    val playlistListUiComponent = PlaylistListUiComponent(navController)
    val searchUiComponent = SearchUiComponent(navController)
    return MainStateHolder(
        trackListUiComponent = trackListUiComponent,
        artistListUiComponent = artistListUiComponent,
        albumListUiComponent = albumListUiComponent,
        genreListUiComponent = genreListUiComponent,
        playlistListUiComponent = playlistListUiComponent,
        searchUiComponent = searchUiComponent
    )
}
