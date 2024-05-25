package com.sebastianvm.musicplayer.features.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sebastianvm.musicplayer.features.album.list.AlbumListStateHolder
import com.sebastianvm.musicplayer.features.album.list.rememberAlbumListStateHolder
import com.sebastianvm.musicplayer.features.artist.list.ArtistListStateHolder
import com.sebastianvm.musicplayer.features.artist.list.rememberArtistListStateHolder
import com.sebastianvm.musicplayer.features.genre.list.GenreListStateHolder
import com.sebastianvm.musicplayer.features.genre.list.rememberGenreListStateHolder
import com.sebastianvm.musicplayer.features.navigation.NavController
import com.sebastianvm.musicplayer.features.playlist.list.PlaylistListStateHolder
import com.sebastianvm.musicplayer.features.playlist.list.rememberPlaylistListStateHolder
import com.sebastianvm.musicplayer.features.search.SearchStateHolder
import com.sebastianvm.musicplayer.features.search.rememberSearchStateHolder
import com.sebastianvm.musicplayer.features.track.list.TrackListArguments
import com.sebastianvm.musicplayer.features.track.list.TrackListStateHolder
import com.sebastianvm.musicplayer.features.track.list.rememberTrackListStateHolder
import com.sebastianvm.musicplayer.player.MediaGroup
import com.sebastianvm.musicplayer.ui.util.mvvm.State
import com.sebastianvm.musicplayer.ui.util.mvvm.StateHolder
import com.sebastianvm.musicplayer.ui.util.mvvm.UserAction
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
    searchStateHolder: SearchStateHolder
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

    override fun handle(action: MainUserAction) = Unit
}

@Composable
fun rememberMainStateHolder(navController: NavController): MainStateHolder {
    val trackListStateHolder =
        rememberTrackListStateHolder(
            args = TrackListArguments(trackListType = MediaGroup.AllTracks),
            navController
        )
    val artistListStateHolder = rememberArtistListStateHolder(navController)
    val albumListStateHolder = rememberAlbumListStateHolder(navController)
    val genreListStateHolder = rememberGenreListStateHolder(navController)
    val playlistListStateHolder = rememberPlaylistListStateHolder()
    val searchStateHolder = rememberSearchStateHolder(navController)
    return remember {
        MainStateHolder(
            trackListStateHolder = trackListStateHolder,
            artistListStateHolder = artistListStateHolder,
            albumListStateHolder = albumListStateHolder,
            genreListStateHolder = genreListStateHolder,
            playlistListStateHolder = playlistListStateHolder,
            searchStateHolder = searchStateHolder
        )
    }
}
