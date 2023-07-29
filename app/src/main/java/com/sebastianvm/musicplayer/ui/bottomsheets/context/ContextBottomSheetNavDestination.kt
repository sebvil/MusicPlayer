package com.sebastianvm.musicplayer.ui.bottomsheets.context

import com.sebastianvm.musicplayer.player.MediaGroup


data class TrackContextMenuArguments(
    val trackId: Long,
    val mediaGroup: MediaGroup,
    val trackIndex: Int = 0,
    val positionInPlaylist: Long? = null
)


data class ArtistContextMenuArguments(val artistId: Long)


data class AlbumContextMenuArguments(val albumId: Long)


data class GenreContextMenuArguments(val genreId: Long)


data class PlaylistContextMenuArguments(val playlistId: Long) 

