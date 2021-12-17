package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R

sealed class ContextMenuItem(@DrawableRes val icon: Int, @StringRes val text: Int) {
    object ViewArtists : ContextMenuItem(R.drawable.ic_artist, R.string.view_artists)
    object ViewAlbums : ContextMenuItem(R.drawable.ic_album, R.string.view_album)
    object Play : ContextMenuItem(R.drawable.ic_play, R.string.play)
    object PlayFromBeginning : ContextMenuItem(R.drawable.ic_play, R.string.play_from_beginning)
    object PlayAllSongs : ContextMenuItem(R.drawable.ic_play, R.string.play_all_songs)
}
