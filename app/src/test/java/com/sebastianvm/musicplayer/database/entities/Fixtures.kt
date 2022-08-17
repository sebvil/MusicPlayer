package com.sebastianvm.musicplayer.database.entities

object Fixtures {
    val albumAlpaca = Album(
        id = C.ID_ONE,
        albumName = C.ALBUM_ALPACA,
        year = C.YEAR_2021,
        artists = C.ARTIST_CAMILO,
        imageUri = C.IMAGE_URI_1
    )

    private val albumBobcat = Album(
        id = C.ID_TWO,
        albumName = C.ALBUM_BOBCAT,
        year = C.YEAR_2022,
        artists = C.ARTIST_ANA,
        imageUri = C.IMAGE_URI_2
    )

    private val albumCheetah = Album(
        id = C.ID_THREE,
        albumName = C.ALBUM_CHEETAH,
        year = C.YEAR_2020,
        artists = C.ARTIST_BOB,
        imageUri = C.IMAGE_URI_3
    )

    val artistAna = Artist(id = C.ID_ONE, artistName = C.ARTIST_ANA)
    private val artistBob = Artist(id = C.ID_TWO, artistName = C.ARTIST_BOB)
    private val artistCamilo = Artist(id = C.ID_THREE, artistName = C.ARTIST_CAMILO)

    val artistWithAlbumsAna = ArtistWithAlbums(
        artist = artistAna,
        artistAlbums = listOf(C.ID_TWO),
        artistAppearsOn = listOf()
    )

    val artistWithAlbumsBob = ArtistWithAlbums(
        artist = artistBob,
        artistAlbums = listOf(C.ID_THREE),
        artistAppearsOn = listOf()
    )

    val artistWithAlbumsCamilo = ArtistWithAlbums(
        artist = artistCamilo,
        artistAlbums = listOf(C.ID_ONE),
        artistAppearsOn = listOf()
    )

    val fullAlbumAlpaca = fullAlbumInfo {
        album = albumAlpaca
    }

    val fullAlbumBobcat = fullAlbumInfo {
        album = albumBobcat
    }

    val fullAlbumCheetah = fullAlbumInfo {
        album = albumCheetah
    }

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

    val playlistApple = Playlist(id = C.ID_ONE, playlistName = C.PLAYLIST_APPLE)


}