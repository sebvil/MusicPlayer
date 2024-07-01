package com.sebastianvm.musicplayer.core.datatest.extensions

import com.sebastianvm.musicplayer.core.model.Album
import com.sebastianvm.musicplayer.core.model.AlbumWithArtists
import com.sebastianvm.musicplayer.core.model.Artist
import com.sebastianvm.musicplayer.core.model.BasicArtist
import com.sebastianvm.musicplayer.core.model.BasicGenre
import com.sebastianvm.musicplayer.core.model.BasicPlaylist
import com.sebastianvm.musicplayer.core.model.BasicTrack
import com.sebastianvm.musicplayer.core.model.Genre
import com.sebastianvm.musicplayer.core.model.Playlist
import com.sebastianvm.musicplayer.core.model.Track

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
