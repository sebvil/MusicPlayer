package com.sebastianvm.musicplayer.util

import com.sebastianvm.model.Album
import com.sebastianvm.model.AlbumWithArtists
import com.sebastianvm.model.Artist
import com.sebastianvm.model.BasicArtist
import com.sebastianvm.model.BasicGenre
import com.sebastianvm.model.BasicPlaylist
import com.sebastianvm.model.BasicTrack
import com.sebastianvm.model.Genre
import com.sebastianvm.model.Playlist
import com.sebastianvm.model.Track

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
