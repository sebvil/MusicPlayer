package com.sebastianvm.musicplayer.ui.library.root

import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.ui.navigation.NavRoutes

sealed class LibraryItem(
    val rowId: String,
    @StringRes val rowName: Int,
    @DrawableRes val icon: Int,
    @PluralsRes val countString: Int,
    open val count: Long
) {
    data class Tracks(override val count: Long) : LibraryItem(
        rowId = NavRoutes.TRACKS_ROOT,
        rowName = R.string.all_songs,
        icon = R.drawable.ic_song,
        countString = R.plurals.number_of_tracks,
        count = count
    )

    data class Artists(override val count: Long) : LibraryItem(
        rowId = NavRoutes.ARTISTS_ROOT,
        rowName = R.string.artists,
        icon = R.drawable.ic_artist,
        countString = R.plurals.number_of_artists,
        count = count
    )

    data class Albums(override val count: Long) : LibraryItem(
        rowId = NavRoutes.ALBUMS_ROOT,
        rowName = R.string.albums,
        icon = R.drawable.ic_album,
        countString = R.plurals.number_of_albums,
        count = count
    )

    data class Genres(override val count: Long) : LibraryItem(
        rowId = NavRoutes.GENRES_ROOT,
        rowName = R.string.genres,
        icon = R.drawable.ic_genre,
        countString = R.plurals.number_of_genres,
        count = count
    )

    data class Playlists(override val count: Long) : LibraryItem(
        rowId = NavRoutes.PLAYLISTS_ROOT,
        rowName = R.string.playlists,
        icon = R.drawable.ic_playlist,
        countString = R.plurals.number_of_playlists,
        count = count
    )
}
