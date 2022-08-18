package com.sebastianvm.musicplayer.database.entities

object Fixtures {
    val albumAlpaca = Album(
        id = C.ID_ONE,
        albumName = C.ALBUM_ALPACA,
        year = C.YEAR_2020,
        artists = C.ARTIST_ANA,
        imageUri = C.IMAGE_URI_1
    )

    val albumBobcat = Album(
        id = C.ID_TWO,
        albumName = C.ALBUM_BOBCAT,
        year = C.YEAR_2021,
        artists = C.ARTIST_BOB,
        imageUri = C.IMAGE_URI_2
    )

    val albumCheetah = Album(
        id = C.ID_THREE,
        albumName = C.ALBUM_CHEETAH,
        year = C.YEAR_2022,
        artists = C.ARTIST_CAMILO,
        imageUri = C.IMAGE_URI_3
    )

    val artistAna = Artist(id = C.ID_ONE, artistName = C.ARTIST_ANA)
    val artistBob = Artist(id = C.ID_TWO, artistName = C.ARTIST_BOB)
    val artistCamilo = Artist(id = C.ID_THREE, artistName = C.ARTIST_CAMILO)

//    val artistWithAlbumsAna = ArtistWithAlbums(
//        artist = artistAna,
//        artistAlbums = listOf(C.ID_TWO),
//        artistAppearsOn = listOf()
//    )
//
//    val artistWithAlbumsBob = ArtistWithAlbums(
//        artist = artistBob,
//        artistAlbums = listOf(C.ID_THREE),
//        artistAppearsOn = listOf()
//    )
//
//    val artistWithAlbumsCamilo = ArtistWithAlbums(
//        artist = artistCamilo,
//        artistAlbums = listOf(C.ID_ONE),
//        artistAppearsOn = listOf()
//    )

    val genreAlpha = Genre(id = C.ID_ONE, genreName = C.GENRE_ALPHA)
    val genreBravo = Genre(id = C.ID_TWO, genreName = C.GENRE_BRAVO)
    val genreCharlie = Genre(id = C.ID_THREE, genreName = C.GENRE_CHARLIE)

    val trackArgentina = Track(
        id = C.ID_ONE,
        trackName = C.TRACK_ARGENTINA,
        albumId = C.ID_ONE,
        albumName = C.ALBUM_ALPACA,
        artists = C.ARTIST_ANA,
        trackDurationMs = 0,
        trackNumber = 0,
        path = ""
    )

    val trackBelgium = Track(
        id = C.ID_TWO,
        trackName = C.TRACK_BELGIUM,
        albumId = C.ID_TWO,
        albumName = C.ALBUM_BOBCAT,
        artists = C.ARTIST_BOB,
        trackDurationMs = 0,
        trackNumber = 0,
        path = ""
    )

    val trackColombia = Track(
        id = C.ID_THREE,
        trackName = C.TRACK_BELGIUM,
        albumId = C.ID_THREE,
        albumName = C.ALBUM_BOBCAT,
        artists = C.ARTIST_BOB,
        trackDurationMs = 0,
        trackNumber = 0,
        path = ""
    )

    val playlistApple = Playlist(id = C.ID_ONE, playlistName = C.PLAYLIST_APPLE)


}