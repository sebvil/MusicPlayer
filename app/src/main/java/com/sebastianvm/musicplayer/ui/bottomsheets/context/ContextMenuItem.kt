package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.model.MediaWithArtists
import com.sebastianvm.musicplayer.ui.icons.Album
import com.sebastianvm.musicplayer.ui.icons.Artist
import com.sebastianvm.musicplayer.ui.icons.Delete
import com.sebastianvm.musicplayer.ui.icons.Genre
import com.sebastianvm.musicplayer.ui.icons.Icons
import com.sebastianvm.musicplayer.ui.icons.PlayArrow
import com.sebastianvm.musicplayer.ui.icons.Playlist
import com.sebastianvm.musicplayer.ui.icons.PlaylistAdd
import com.sebastianvm.musicplayer.ui.icons.PlaylistRemove
import com.sebastianvm.musicplayer.ui.icons.QueueAdd
import com.sebastianvm.musicplayer.ui.util.compose.IconState

sealed class ContextMenuItem(val icon: IconState, @StringRes val text: Int) {
    data class ViewArtists(val mediaType: MediaWithArtists, val mediaId: Long) :
        ContextMenuItem(Icons.Artist, R.string.view_artists)

    data class ViewArtist(val artistId: Long) : ContextMenuItem(Icons.Artist, R.string.view_artist)
    data class ViewAlbum(val albumId: Long) : ContextMenuItem(Icons.Album, R.string.view_album)
    data class ViewGenre(val genreId: Long) : ContextMenuItem(Icons.Genre, R.string.view_genre)
    data class ViewPlaylist(val playlistId: Long) :
        ContextMenuItem(Icons.Playlist, R.string.view_playlist)

    data object Play : ContextMenuItem(Icons.PlayArrow, R.string.play)
    data object PlayFromBeginning :
        ContextMenuItem(Icons.PlayArrow, R.string.play_from_beginning)

    data object PlayAllSongs : ContextMenuItem(Icons.PlayArrow, R.string.play_all_songs)
    data object AddToQueue : ContextMenuItem(Icons.QueueAdd, R.string.add_to_queue)
    data object DeletePlaylist : ContextMenuItem(Icons.Delete, R.string.delete_playlist)
    data object RemoveFromPlaylist :
        ContextMenuItem(Icons.PlaylistRemove, R.string.remove_from_playlist)

    data object AddToPlaylist : ContextMenuItem(Icons.PlaylistAdd, R.string.add_to_playlist)
}
