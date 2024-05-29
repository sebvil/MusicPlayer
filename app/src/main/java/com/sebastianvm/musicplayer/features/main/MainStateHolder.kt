package com.sebastianvm.musicplayer.features.main

import android.util.Log
import com.sebastianvm.musicplayer.di.DependencyContainer
import com.sebastianvm.musicplayer.features.album.list.AlbumListStateHolder
import com.sebastianvm.musicplayer.features.album.list.getAlbumListStateHolder
import com.sebastianvm.musicplayer.features.artist.list.ArtistListStateHolder
import com.sebastianvm.musicplayer.features.artist.list.getArtistListStateHolder
import com.sebastianvm.musicplayer.features.genre.list.GenreListStateHolder
import com.sebastianvm.musicplayer.features.genre.list.getGenreListStateHolder
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistListStateHolder
import com.sebastianvm.musicplayer.features.playlist.list.getPlaylistListStateHolder
import com.sebastianvm.musicplayer.features.search.SearchStateHolder
import com.sebastianvm.musicplayer.features.search.getSearchStateHolder
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListStateHolder
import com.sebastianvm.musicplayer.features.track.list.getTrackListStateHolder
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
    val trackListStateHolder: TrackListStateHolder,
    val artistListStateHolder: ArtistListStateHolder,
    val albumListStateHolder: AlbumListStateHolder,
    val genreListStateHolder: GenreListStateHolder,
    val playlistListStateHolder: PlaylistListStateHolder,
    val searchStateHolder: SearchStateHolder
) : State

sealed interface MainUserAction : UserAction

class MainStateHolder(
    trackListStateHolder: TrackListStateHolder,
    artistListStateHolder: ArtistListStateHolder,
    albumListStateHolder: AlbumListStateHolder,
    genreListStateHolder: GenreListStateHolder,
    playlistListStateHolder: PlaylistListStateHolder,
    searchStateHolder: SearchStateHolder,
    override val stateHolderScope: CoroutineScope = stateHolderScope()
) : StateHolder<MainState, MainUserAction> {

    private val _state = MutableStateFlow(
        MainState(
            trackListStateHolder,
            artistListStateHolder,
            albumListStateHolder,
            genreListStateHolder,
            playlistListStateHolder,
            searchStateHolder
        )
    )
    override val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    init {
        Log.i("MAIN", "initializing")
    }

    override fun handle(action: MainUserAction) = Unit
}

fun getMainStateHolder(
    dependencies: DependencyContainer,
    navController: NavController
): MainStateHolder {
    val trackListStateHolder = getTrackListStateHolder(
        dependencies = dependencies,
        args = TrackListArguments(trackListType = MediaGroup.AllTracks),
        navController = navController
    )
    val artistListStateHolder = getArtistListStateHolder(dependencies, navController)
    val albumListStateHolder = getAlbumListStateHolder(dependencies, navController)
    val genreListStateHolder = getGenreListStateHolder(dependencies, navController)
    val playlistListStateHolder = getPlaylistListStateHolder(dependencies, navController)
    val searchStateHolder = getSearchStateHolder(dependencies, navController)
    return MainStateHolder(
        trackListStateHolder = trackListStateHolder,
        artistListStateHolder = artistListStateHolder,
        albumListStateHolder = albumListStateHolder,
        genreListStateHolder = genreListStateHolder,
        playlistListStateHolder = playlistListStateHolder,
        searchStateHolder = searchStateHolder
    )
}
