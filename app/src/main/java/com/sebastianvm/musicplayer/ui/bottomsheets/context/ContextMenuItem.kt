package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R

sealed class ContextMenuItem(@DrawableRes val icon: Int, @StringRes val text: Int) {
    data object ViewArtists : ContextMenuItem(R.drawable.ic_artist, R.string.view_artists)
    data object ViewArtist : ContextMenuItem(R.drawable.ic_artist, R.string.view_artist)
    data object ViewAlbum : ContextMenuItem(R.drawable.ic_album, R.string.view_album)
    data object ViewGenre : ContextMenuItem(R.drawable.ic_genre, R.string.view_genre)
    data object ViewPlaylist : ContextMenuItem(R.drawable.ic_playlist, R.string.view_playlist)
    data object Play : ContextMenuItem(R.drawable.ic_play, R.string.play)
    data object PlayFromBeginning :
        ContextMenuItem(R.drawable.ic_play, R.string.play_from_beginning)

    data object PlayAllSongs : ContextMenuItem(R.drawable.ic_play, R.string.play_all_songs)
    data object AddToQueue : ContextMenuItem(R.drawable.ic_queue, R.string.add_to_queue)
    data object DeletePlaylist : ContextMenuItem(R.drawable.ic_close, R.string.delete_playlist)
    data object RemoveFromPlaylist :
        ContextMenuItem(R.drawable.ic_close, R.string.remove_from_playlist)

    data object AddToPlaylist : ContextMenuItem(R.drawable.ic_plus, R.string.add_to_playlist)
}
