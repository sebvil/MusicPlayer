package com.sebastianvm.musicplayer.util

import com.sebastianvm.musicplayer.model.Album
import com.sebastianvm.musicplayer.model.AlbumWithArtists
import com.sebastianvm.musicplayer.model.Artist
import com.sebastianvm.musicplayer.model.BasicArtist
import com.sebastianvm.musicplayer.model.BasicGenre
import com.sebastianvm.musicplayer.model.BasicPlaylist
import com.sebastianvm.musicplayer.model.BasicTrack
import com.sebastianvm.musicplayer.model.Genre
import com.sebastianvm.musicplayer.model.Playlist
import com.sebastianvm.musicplayer.model.Track

fun Album.toAlbumWithArtists(): AlbumWithArtists {
    return AlbumWithArtists(
        id = id,
        title = title,
        imageUri = imageUri,
        artists = artists,
        year = year,
    )
}

fun Genre.toBasicGenre(): BasicGenre {
    return BasicGenre(id = id, name = name)
}

fun Playlist.toBasicPlaylist(): BasicPlaylist {
    return BasicPlaylist(id = id, name = name)
}

fun Track.toBasicTrack(): BasicTrack {
    return BasicTrack(id = id, name = name, artists = artists.joinToString { it.name })
}

fun Artist.toBasicArtist(): BasicArtist {
    return BasicArtist(id = id, name = name)
}
