package com.sebastianvm.musicplayer.ui.bottomsheets.context

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sebastianvm.musicplayer.R
import com.sebastianvm.musicplayer.player.MediaType

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


fun contextMenuItemsForMedia(mediaType: MediaType, mediaGroupType: MediaType, artistCount: Int = 0): List<ContextMenuItem> {
    return when (mediaType) {
        MediaType.ALL_TRACKS, MediaType.SINGLE_TRACK -> {
            if (mediaGroupType == MediaType.ALBUM) {
                listOf(
                    ContextMenuItem.Play,
                    ContextMenuItem.AddToQueue,
                    if (artistCount == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                )
            } else {
                listOf(
                    ContextMenuItem.Play,
                    ContextMenuItem.AddToQueue,
                    if (artistCount == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                    ContextMenuItem.ViewAlbum
                )
            }
        }
        MediaType.ARTIST -> {
            listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewArtist
            )
        }
        MediaType.ALBUM -> {
            listOf(
                ContextMenuItem.PlayFromBeginning,
                if (artistCount == 1) ContextMenuItem.ViewArtist else ContextMenuItem.ViewArtists,
                ContextMenuItem.ViewAlbum
            )
        }
        MediaType.GENRE -> {
            listOf(
                ContextMenuItem.PlayAllSongs,
                ContextMenuItem.ViewGenre
            )
        }
        MediaType.UNKNOWN -> listOf()
    }
}
