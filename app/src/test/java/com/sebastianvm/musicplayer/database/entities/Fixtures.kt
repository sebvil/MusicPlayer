package com.sebastianvm.musicplayer.database.entities

object Fixtures {
    val albumAlpaca = Album(
        id = C.ID_ONE,
        albumName = C.ALBUM_ALPACA,
        year = C.YEAR_2021,
        artists = C.ARTIST_CAMILO
    )

    val albumBobcat = Album(
        id = C.ID_TWO,
        albumName = C.ALBUM_BOBCAT,
        year = C.YEAR_2022,
        artists = C.ARTIST_ANA
    )

    val albumCheetah = Album(
        id = C.ID_THREE,
        albumName = C.ALBUM_CHEETAH,
        year = C.YEAR_2020,
        artists = C.ARTIST_BOB
    )

    val artistAna = Artist(id = C.ID_ONE, artistName = C.ARTIST_ANA)
    val artistBob = Artist(id = C.ID_TWO, artistName = C.ARTIST_BOB)
    val artistCamilo = Artist(id = C.ID_THREE, artistName = C.ARTIST_CAMILO)

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

}