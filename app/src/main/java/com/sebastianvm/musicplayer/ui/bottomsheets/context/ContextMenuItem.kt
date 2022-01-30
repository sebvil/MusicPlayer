package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R

sealed class ContextMenuItem(@DrawableRes val icon: Int, @StringRes val text: Int) {
    object ViewArtists : ContextMenuItem(R.drawable.ic_artist, R.string.view_artists)
    object ViewArtist : ContextMenuItem(R.drawable.ic_artist, R.string.view_artist)
    object ViewAlbum : ContextMenuItem(R.drawable.ic_album, R.string.view_album)
    object ViewGenre : ContextMenuItem(R.drawable.ic_genre, R.string.view_genre)
    object Play : ContextMenuItem(R.drawable.ic_play, R.string.play)
    object PlayFromBeginning : ContextMenuItem(R.drawable.ic_play, R.string.play_from_beginning)
    object PlayAllSongs : ContextMenuItem(R.drawable.ic_play, R.string.play_all_songs)
    object AddToQueue : ContextMenuItem(R.drawable.ic_queue, R.string.add_to_queue)
}
